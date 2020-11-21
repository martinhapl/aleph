package net.hapl.aleph.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.factory.EnvelopeDownload;
import net.hapl.aleph.factory.FindXMLParser;
import net.hapl.aleph.factory.PresentXMLParser;
import net.hapl.aleph.model.FindDTO;
import net.hapl.aleph.model.PresentDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FindRepository {

    private FindDTO findDTO;
    private String query;

    private List<PresentDTO> presentDTOs;

    private int pocet_polozek;
    private int max_pocet;
    private int aktual_pozice;

    private static final String TAG = "FindRepository";

    public FindRepository() {
        findDTO = new FindDTO();
        presentDTOs = new ArrayList();

        pocet_polozek = 10;
        aktual_pozice = 1;
        max_pocet = 0;
    }

    public List<PresentDTO> find(String hledanyAutor, Context context) {
        this.query = hledanyAutor;

        Log.d(TAG, "find: " + query);

        presentDTOs.clear();
        if (AlephControl.getInstance().isNetworkOnline()) {
            aktual_pozice = 1;

            FindXMLParser parser = new FindXMLParser(hledanyAutor, AlephControl.getInstance().getServerConfig());
            if (parser.getDTOs().size()!=0) {
                findDTO = parser.getDTOs().get(0);

                PresentXMLParser presentParser = new PresentXMLParser(
                        findDTO, aktual_pozice, aktual_pozice + pocet_polozek,
                        AlephControl.getInstance().getServerConfig());
                presentDTOs = presentParser.getDTOs();
                if (findDTO.getNo_record()==null){
                    max_pocet = 0;
                } else {
                    max_pocet = Integer.parseInt(findDTO.getNo_record());
                }
                //hAutor = hledanyAutor;
                aktual_pozice = aktual_pozice + presentDTOs.size();

                // volani AsyncTasku pro nacteni obalek
                new EnvelopeDownload(context).execute(presentDTOs);

                return presentParser.getDTOs();
            } else {
                findDTO = new FindDTO();
                return presentDTOs;
            }
        } else {
            return presentDTOs;
        }
    }

    public List<PresentDTO> findNext() {
        //nalezeni dalsich x(pocet_polozek) polozek
        List<PresentDTO> presentDTOsDavka = new ArrayList<PresentDTO>();

        if (AlephControl.getInstance().isNetworkOnline()) {
            if (aktual_pozice < max_pocet) {
                //hledani podle findDTO.getSet_number(); a ne znova cely dokola

                PresentXMLParser parserDotazu = new PresentXMLParser(findDTO,
                        aktual_pozice, aktual_pozice + pocet_polozek,
                        AlephControl.getInstance().getServerConfig());

                presentDTOsDavka = parserDotazu.getDTOs();
                presentDTOs.addAll(presentDTOsDavka);

                aktual_pozice = aktual_pozice + presentDTOsDavka.size();

                // volani AsyncTasku pro nacteni obalek
                new EnvelopeDownload(MainActivity.getContext()).execute(presentDTOs);
            }
        }
        return presentDTOsDavka;
    }

    public List<PresentDTO> next() {

        List<PresentDTO> presentDTOsDavka = new ArrayList();

        for(int i = 0; i < pocet_polozek; i++) {
            presentDTOsDavka.add(presentDTOs.get(aktual_pozice+i));
            aktual_pozice++;
        }

        return presentDTOsDavka;
    }

    public List<PresentDTO> preview() {
        List<PresentDTO> presentDTOsDavka = new ArrayList();

        for(int i = 0; i < pocet_polozek; i++){
            presentDTOsDavka.add(presentDTOs.get(aktual_pozice-i));
            aktual_pozice--;
        }

        return presentDTOsDavka;
    }

    public List<PresentDTO> first() {
        pocet_polozek = 10;
        aktual_pozice = 1;
        List<PresentDTO> presentDTOsDavka = new ArrayList();

        for(int i = 0; i < pocet_polozek; i++){
            presentDTOsDavka.add(presentDTOs.get(aktual_pozice+i));
            aktual_pozice++;
        }
        return presentDTOsDavka;
    }

    public Bitmap nactiObalku(int indexPresentDTO) {
        //nacteni obalky
        Bitmap obalkaTmp = null;
        obalkaTmp = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
        String isbnTmp;
        isbnTmp = presentDTOs.get(indexPresentDTO).getObalka();
        if (presentDTOs.get(indexPresentDTO).getObalka()!=null) {
            if (existObalkaInCache(isbnTmp)) {
                HttpAsyncTaskEnvelope myHttpAsyncTaskob = new HttpAsyncTaskEnvelope();
                myHttpAsyncTaskob.execute("http://www.obalkyknih.cz/api/cover?isbn="+ isbnTmp);
                try {
                    obalkaTmp = myHttpAsyncTaskob.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(obalkaTmp.getByteCount()>1) {
                    saveImageToDiskCache(isbnTmp, obalkaTmp);
                } else {
                    obalkaTmp = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
                    presentDTOs.get(indexPresentDTO).setObalka(null);
                }
            } else {
                obalkaTmp = getImageFromDiskCache(isbnTmp);
            }
        }
        return obalkaTmp;
    }

    public boolean existObalkaInCache(String isbn) {

        File file = new File(MainActivity.IMAGECACHEDIR, isbn + ".png");
        return !file.exists();
    }

    public Bitmap getImageFromDiskCache(String obalka) {
        try {
            String fileName = MainActivity.IMAGECACHEDIR+File.separator+obalka+".png";
            File f = new File(fileName);
            if (!f.exists()) {
                return BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
            } else {
                Bitmap tmp = BitmapFactory.decodeFile(fileName);
                return tmp;
            }
        } catch (Exception e) {
            return BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
        }
    }


    public void saveImageToDiskCache(String obalka, Bitmap ukladanaObalka) {

        if(!MainActivity.IMAGECACHEDIR.exists()) MainActivity.IMAGECACHEDIR.mkdirs();

        File file = new File(MainActivity.IMAGECACHEDIR, obalka + ".png");
        if (!file.exists()&&ukladanaObalka.getByteCount() > 1) {

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);

                ukladanaObalka.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public List<PresentDTO> getPresentDTOs() {
        return presentDTOs;
    }

    public void resetPresentDTOs() {
        this.presentDTOs.clear();
    }

    public FindDTO getFindDTO() {
        return findDTO;
    }

    public String getQuery() {
        return this.query;
    }

}
