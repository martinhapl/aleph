package net.hapl.aleph.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.factory.CheckCtenarXMLParser;
import net.hapl.aleph.factory.CtenarXMLParser;
import net.hapl.aleph.factory.FindXMLParser;
import net.hapl.aleph.factory.HoldReqCancelXMLParser;
import net.hapl.aleph.factory.HoldReqXMLParser;
import net.hapl.aleph.factory.HttpFactory;
import net.hapl.aleph.factory.ItemDataXMLParser;
import net.hapl.aleph.factory.ObalkaDownload;
import net.hapl.aleph.factory.PresentXMLParser;
import net.hapl.aleph.factory.RenewXMLParser;
import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.CtenarDTO;
import net.hapl.aleph.model.FindDTO;
import net.hapl.aleph.model.ItemDataDTO;
import net.hapl.aleph.model.PresentDTO;
import net.hapl.aleph.model.PresentDTOs;
import net.hapl.aleph.model.QueryDTO;
import net.hapl.aleph.model.RezervaceDTO;
import net.hapl.aleph.model.ServerConfigDTO;
import net.hapl.aleph.model.VypujckaDTO;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlephControl {

    private static final String TAG = "AlephControl";

    private List<PresentDTO> presentDTOs;
    private FindDTO findDTO;
    private UserConfigDTO userConfigDTO;
    private ServerConfigDTO serverConfigDTO;
    private List<PresentDTO> favouriteDTOs;
    private CtenarDTO ctenarDTO;
    private List<String> favouriteQuery;
    private List<ItemDataDTO> itemDataDTOs;

    private int pocet_polozek;
    private int max_pocet;
    private int aktual_pozice;
    private String hAutor;

    private String query;

    private static AlephControl instance;

    public static AlephControl getInstance() {
        if(instance == null)
            instance = new AlephControl();
        return instance;
    }

    public AlephControl() {
        super();

        pocet_polozek = 10;
        aktual_pozice = 1;
        max_pocet = 0;
        findDTO = new FindDTO();
        userConfigDTO = new UserConfigDTO();
        serverConfigDTO = getServerConfig();;
        presentDTOs = new ArrayList<PresentDTO>();
        favouriteDTOs = new ArrayList<PresentDTO>();
        ctenarDTO = new CtenarDTO();
        favouriteQuery = new ArrayList<String>();
        itemDataDTOs = new ArrayList<ItemDataDTO>();


        // loadFavourites();
    }

    public void setPocet_polozek(int pocet) {
        pocet_polozek = pocet;
    }

    public int getPocet_polozek() {
        return pocet_polozek;
    }

    public int getMax_pocet() {
        return max_pocet;
    }

    public String getQuery() {
        return this.query;
    }

    public List<PresentDTO> find(String hledanyAutor, Context context) {
        this.query = hledanyAutor;

        Log.d(TAG, "find: " + query);

        presentDTOs.clear();
        if (isNetworkOnline()) {
            aktual_pozice = 1;

            FindXMLParser parser = new FindXMLParser(hledanyAutor, serverConfigDTO);
            if (parser.getDTOs().size()!=0) {
                findDTO = parser.getDTOs().get(0);

                PresentXMLParser parserDotazu = new PresentXMLParser(findDTO,String.format("%09d", aktual_pozice),String.format("%09d", aktual_pozice + pocet_polozek), serverConfigDTO);
                presentDTOs = parserDotazu.getDTOs();
                if (findDTO.getNo_record()==null){
                    max_pocet = 0;
                }
                else{
                    max_pocet = Integer.parseInt(findDTO.getNo_record());
                }
                hAutor = hledanyAutor;
                aktual_pozice = aktual_pozice + presentDTOs.size();

                // volani AsyncTasku pro nacteni obalek
                new ObalkaDownload(context).execute(presentDTOs);

                return parserDotazu.getDTOs();
            }
            else {
                findDTO = new FindDTO();
                return presentDTOs;
            }
        }
        else {
            return presentDTOs;
        }
    }

    public List<PresentDTO> findNext() {
        //nalezeni dalsich x(pocet_polozek) polozek

        List<PresentDTO> presentDTOsDavka = new ArrayList<PresentDTO>();

        if (isNetworkOnline()) {
            if (aktual_pozice<max_pocet) {
                //hledani podle findDTO.getSet_number(); a ne znova cely dokola

                PresentXMLParser parserDotazu = new PresentXMLParser(findDTO,String.format("%09d", aktual_pozice),String.format("%09d", aktual_pozice + pocet_polozek), serverConfigDTO);
                presentDTOsDavka = parserDotazu.getDTOs();
                presentDTOs.addAll(presentDTOsDavka);

                aktual_pozice = aktual_pozice + presentDTOsDavka.size();

                // volani AsyncTasku pro nacteni obalek
                new ObalkaDownload(MainActivity.getContext()).execute(presentDTOs);
            }
        }
        return presentDTOsDavka;
    }

    public List<PresentDTO> next() {

        List<PresentDTO> presentDTOsDavka = new ArrayList<PresentDTO>();

        for(int i = 0; i < pocet_polozek; i++){
            presentDTOsDavka.add(presentDTOs.get(aktual_pozice+i));
            aktual_pozice++;
        }

        return presentDTOsDavka;
    }

    public List<PresentDTO> preview() {

        List<PresentDTO> presentDTOsDavka = new ArrayList<PresentDTO>();

        for(int i = 0; i < pocet_polozek; i++){
            presentDTOsDavka.add(presentDTOs.get(aktual_pozice-i));
            aktual_pozice--;
        }

        return presentDTOsDavka;
    }

    public List<PresentDTO> first() {

        pocet_polozek = 10;
        aktual_pozice = 1;
        List<PresentDTO> presentDTOsDavka = new ArrayList<PresentDTO>();

        for(int i = 0; i < pocet_polozek; i++){
            presentDTOsDavka.add(presentDTOs.get(aktual_pozice+i));
            aktual_pozice++;
        }
        return presentDTOsDavka;
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

    public void setConfig(String name, String email, String login, String passwd) {
        //nastaveni uzivatelskeho nastaveni

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        UserConfigDTO userConfigDTOtest = new UserConfigDTO( name, email, login, passwd);
        File result = new File(MainActivity.DIRECTORY.getPath()+File.separator+"config.xml");

        if (result.exists()) {
            try {
                serializer.write(userConfigDTOtest, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                result.createNewFile();
                serializer.write(userConfigDTOtest, result);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UserConfigDTO getConfig() {
        //nacteni uzivatelskoho nastaveni

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath(), "config.xml");

        if (source.exists()) {
            try {
                userConfigDTO = serializer.read(UserConfigDTO.class, source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userConfigDTO;
    }

    public ServerConfigDTO getServerConfig() {
      //TODO: predelat na preference
        serverConfigDTO = new ServerConfigDTO("MOBAPP","MOBAPP","https://aleph.techlib.cz/X","stk","hka50");
        return serverConfigDTO;
    }

    public void removeFavourite(int position) {
        favouriteDTOs.remove(position);
        saveFavourites();
    }

    public Boolean addFavourite(PresentDTO favouriteDTO) {
        loadFavourites();

        //pridani polozky do oblibenych
        Boolean neniVOblibenych;
        neniVOblibenych = true;
        for (int j=0;j<favouriteDTOs.size();j++){
            if (favouriteDTOs.get(j).getDoc_number().equals(favouriteDTO.getDoc_number()))
            {
                neniVOblibenych = false;
            }
        }
        if (neniVOblibenych) {
            favouriteDTOs.add(favouriteDTO);
            saveFavourites();
        }

        return neniVOblibenych;

    }

    public List<PresentDTO> getFavourite() {
        loadFavourites();
        return favouriteDTOs;
    }

    public void loadFavourites() {
        //nacteni ulozenych oblibenich z favourite.xml do DTOs

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath(), "favourite.xml");

        PresentDTOs presentDTOsTest = new PresentDTOs();

        if (source.exists()) {
            try {
                if (!(source.length() == 0)) {
                    presentDTOsTest = serializer.read(PresentDTOs.class, source);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favouriteDTOs = presentDTOsTest.getPresentDTOs();
    }

    public void saveFavourites() {
        //ulozeni oblibenych do favourite.xml

        File result = new File(MainActivity.DIRECTORY.getPath(), "favourite.xml");

        PresentDTOs presentDTOsTest = new PresentDTOs();
        presentDTOsTest.setPresentDTOs(favouriteDTOs);

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);

        if (result.exists()) {
            try {
                serializer.write(presentDTOsTest, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                result.createNewFile();
                serializer.write(presentDTOsTest, result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getImageFromDiskCache(String obalka) {
        try {
            String fileName = MainActivity.IMAGECACHEDIR+File.separator+obalka+".png";
            File f = new File(fileName);
            if (!f.exists()) {
                return BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
            }
            else {
                Bitmap tmp = BitmapFactory.decodeFile(fileName);
                return tmp;
            }
        } catch (Exception e) {
            return BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
        }
    }

    public CtenarDTO loadCtenar() {

        getConfig();

        if (isNetworkOnline()) {
            CtenarXMLParser parser = new CtenarXMLParser(userConfigDTO, serverConfigDTO);
            ctenarDTO = parser.getDTOs().get(0);
        }

        return ctenarDTO;
    }

    public CtenarDTO getCtenar() {
        return ctenarDTO;
    }

    public void removeFavouriteQuery(int position) {
        this.favouriteQuery.remove(position);
        saveFavoritesQuery();
    }

    public Boolean addFavouriteQuery(String query) {

        loadFavoritesQuery();

        Boolean neniVOblibenych;
        neniVOblibenych = true;
        for (int j=0;j<this.favouriteQuery.size();j++){
            if (this.favouriteQuery.get(j).equals(query))
            {
                neniVOblibenych = false;
            }
        }
        if (neniVOblibenych) {
            this.favouriteQuery.add(query);
            saveFavoritesQuery();
        }

        return neniVOblibenych;
    }

    public List<String> getFavouriteQuery() {
        loadFavoritesQuery();
        return this.favouriteQuery;
    }

    public void loadFavoritesQuery() {
        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath(), "favouriteQuery.xml");

        List<String> queryList = new ArrayList<String>();

        if (source.exists()) {
            try {
                queryList = serializer.read(QueryDTO.class, source).getQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.favouriteQuery = queryList;
    }

    public void saveFavoritesQuery() {
        File result = new File(MainActivity.DIRECTORY.getPath(), "favouriteQuery.xml");

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);

        QueryDTO queryDTO = new QueryDTO(this.favouriteQuery);

        if (result.exists()) {
            try {
                serializer.write(queryDTO, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                result.createNewFile();
                serializer.write(queryDTO, result);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkUserConfig() {
        if (isNetworkOnline()) {
            CheckCtenarXMLParser parserCheckCtenar = new CheckCtenarXMLParser(userConfigDTO, serverConfigDTO);

            if (parserCheckCtenar.getErrorAuth()==null){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

    }

    public Boolean deleteFavourites() {

        //smazani oblibenych

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath()+File.separator+"favourite.xml");

        PresentDTOs presentDTOsTmp = new PresentDTOs();

        if (source.exists()) {
            try {
                if (!(source.length() == 0)) {
                    source.delete();
                    source.createNewFile();
                    serializer.write(presentDTOsTmp, source);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        favouriteDTOs = presentDTOsTmp.getPresentDTOs();
        return true;
    }

    public void logOffUser(){
        //smazani configu

        setConfig(null,  null, null, null);
        userConfigDTO = new UserConfigDTO();

        //smazani oblibenych dotazu a knih

        //deleteFavourites();
        // ctenarDTO = new CtenarDTO();
    }

    public  List<ItemDataDTO> loadItemData(PresentDTO document){
        //nacteni dat k dokumetnu (dostupnost)

        ItemDataXMLParser parserItemData = new ItemDataXMLParser(serverConfigDTO, document);
        itemDataDTOs = parserItemData.getItemDataDTOs();
        return itemDataDTOs;

    }

    public Boolean createHold(ItemDataDTO holdItem){
        //vytvoreni rezervace

        HoldReqXMLParser parserHoldReq = new HoldReqXMLParser(userConfigDTO, serverConfigDTO, holdItem);
        if (!(parserHoldReq.getReply()==null)){
            if (parserHoldReq.getReply().equals("ok")){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public Boolean cancelHold(RezervaceDTO holdItem){
        //zruseni rezervace

        HoldReqCancelXMLParser parserHoldReqCancel = new HoldReqCancelXMLParser(userConfigDTO, serverConfigDTO, holdItem);
        if (!(parserHoldReqCancel.getReply()==null)){
            if (parserHoldReqCancel.getReply().equals("ok")){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }


    public Boolean renewLoan(VypujckaDTO pujcenaKniha){
        //obnoveni vypujcky
        RenewXMLParser parserRenew = new RenewXMLParser(userConfigDTO, serverConfigDTO, pujcenaKniha);
        if (parserRenew.getReply().equals("ok")){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isNetworkOnline() {

        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) MainActivity.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public Boolean existObalkaInCache(String isbn) {

        File file = new File(MainActivity.IMAGECACHEDIR, isbn + ".png");
        if(!file.exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void saveImageToDiskCache(String obalka, Bitmap ukladanaObalka) {

        if(!MainActivity.IMAGECACHEDIR.exists()) MainActivity.IMAGECACHEDIR.mkdirs();

        File file = new File(MainActivity.IMAGECACHEDIR, obalka + ".png");
        if(!file.exists()&&ukladanaObalka.getByteCount()>1) {

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

    public Bitmap nactiObalku(int indexPresentDTO) {
        //nacteni obalky
        Bitmap obalkaTmp = null;
        obalkaTmp = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
        String isbnTmp;
        isbnTmp = presentDTOs.get(indexPresentDTO).getObalka();
        if (presentDTOs.get(indexPresentDTO).getObalka()!=null)
        {
            if (existObalkaInCache(isbnTmp)) {
                HttpAsyncTaskObalka myHttpAsyncTaskob = new HttpAsyncTaskObalka();
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
                }
                else {
                    obalkaTmp = BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium);
                    presentDTOs.get(indexPresentDTO).setObalka(null);
                }
            }
            else {
                obalkaTmp = getImageFromDiskCache(isbnTmp);
            }
        }
        return obalkaTmp;
    }


    private class HttpAsyncTaskObalka extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap tempBitmap=null;
            try {
                tempBitmap = HttpFactory.downloadBitmap(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  tempBitmap;
        }
    }

    public String createSFX(PresentDTO aktualniKniha) {
        String urlSFX = null;
        String sid = null;
        String genre = null;
        String isbn = null;
        String issn = null;
        String date = null;
        String volume = null;
        String issue = null;
        String spage = null;
        String aulast = null;
        String aufirst = null;
        String title = null;
        String atitle = null;

        urlSFX = "http://sfx.is.cuni.cz/sfxlcl3?";
        //http://sfx.techlib.cz/sfxlcl41
        if (date == null) {
            urlSFX = urlSFX + "&sfx.ignore_date_threshold=1";
        }
        try {

            if (aktualniKniha.getIsbn() != null) {
                isbn = URLEncoder.encode(aktualniKniha.getIsbn(), "UTF-8");
                urlSFX = urlSFX + "&isbn=" + isbn;
            }
            if (aktualniKniha.getImprintRok() != null) {
                date = URLEncoder.encode(aktualniKniha.getImprintRok(), "UTF-8");
                urlSFX = urlSFX + "&date=" + date;
            }
            if (aktualniKniha.getIsbn() != null) {
                issn = URLEncoder.encode(aktualniKniha.getIsbn(), "UTF-8");
                urlSFX = urlSFX + "&issn=" + issn;
            }
            if (aktualniKniha.getNazev() != null) {
                title = URLEncoder.encode(aktualniKniha.getNazev(), "UTF-8");
                urlSFX = urlSFX + "&title=" + title;
            }
//            if (aktualniKniha.getAutor() != null) {
//                aufirst = aktualniKniha.getAutor();
//                if (aufirst.indexOf(",") + 2 < aufirst.indexOf(",", aufirst.indexOf(",") + 1)) {
//                    aufirst = aufirst.substring(aufirst.indexOf(",") + 2, aufirst.indexOf(",", aufirst.indexOf(",") + 1));
//                }
//                aufirst = URLEncoder.encode(aufirst, "UTF-8");
//                urlSFX = urlSFX + "&aufirst=" + aufirst;
//            }
            if (aktualniKniha.getAutor() != null) {
                aulast = aktualniKniha.getAutor();
                if (aulast.indexOf(",") > 0) {
                    aulast = aulast.substring(0,aulast.indexOf(","));
                }
                aulast = URLEncoder.encode(aulast, "UTF-8");
                urlSFX = urlSFX + "&aulast=" + aulast;
            }
            urlSFX = urlSFX + "&sid=" + MainActivity.SFX_SID;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // urlSFX = urlSFX + "&isbn=" + isbn + "&date=" + date + "&sid=" + sid + "&genre=" + genre + "&issn=" + issn + "&volume=" + volume
        //         + "&issue=" + issue + "&spage=" + spage + "&aulast=" + aulast + "&aufirst=" + aufirst + "&title=" + title + "&atitle=" + atitle;

        return urlSFX;

    }

    public String createMessageToSendByEmail(int recordId, int typeRecord) {
        PresentDTO presentDTO;
        String messageToSend;

        if(typeRecord == MainActivity.DETAIL_SEARCH_STATE) {
            presentDTO = AlephControl.getInstance().getPresentDTOs().get(recordId);
        }
        else {
            presentDTO = AlephControl.getInstance().getFavourite().get(recordId);
        }

        messageToSend = MainActivity.getContext().getString(R.string.email_share_content) + "\n\n";
        if(presentDTO.getNazev() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.titul) + ": " + presentDTO.getNazev() + "\n";
        }
        if(presentDTO.getAutor() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.author) + ": " + presentDTO.getAutor() + "\n";
        }
        if(presentDTO.getImprint() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.nakladatel_udaje) + ": " + presentDTO.getImprint() + "\n";
        }
        if(presentDTO.getVydani() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.vydani) + ": " + presentDTO.getVydani() + "\n";
        }
        if(presentDTO.getPopis() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.fyzicky_popis) + ": " + presentDTO.getPopis() + "\n";
        }
        if(presentDTO.getPoznamka() != null && presentDTO.getPoznamka().size() != 0) {
            messageToSend += MainActivity.getContext().getString(R.string.poznamky);

            for(String poznamka : presentDTO.getPoznamka()) {
                messageToSend += ": " + poznamka + "\n";
            }
        }
        if(presentDTO.getPredmet() != null && presentDTO.getPredmet().size() != 0) {
            messageToSend += MainActivity.getContext().getString(R.string.predmet);

            for(String predmet : presentDTO.getPoznamka()) {
                messageToSend += ": " + predmet + "\n";
            }
        }
        if(presentDTO.getIsbn() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.isbn) + ": " + presentDTO.getIsbn() + "\n";
        }
        if(presentDTO.getJazyk() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.jazyk) + ": " + presentDTO.getJazyk() + "\n";
        }

        return messageToSend;
    }
}