package net.hapl.aleph.factory;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.model.FindDTO;
import net.hapl.aleph.model.PresentDTO;
import net.hapl.aleph.model.ServerConfigDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.xml.sax.Attributes;


public class PresentXMLParser extends AbstractXmlParseDefaultHandler {

    private final List<PresentDTO> presentDTOs;
    private PresentDTO presentDTO;
    private final FindDTO findDTO;
    private String hledanyAutor;
    private String tmpValue;
    private String varfieldID;
    private String subfieldLabel;
    private final Bitmap image = null;
    private List<String> poznamky;
    private List<String> predmety;
    private StringBuilder builder;

    public PresentXMLParser(FindDTO hledanyAutor, int zacatek, int konec, ServerConfigDTO serverConfigDTO) {
        presentDTOs = new ArrayList<>();
        findDTO = hledanyAutor;
        String kodHledani = findDTO.getSet_number();
        String entryNum = findDTO.getNo_record();
        String entryZacatek = String.format("%09d", zacatek);
        String entryKonec = String.format("%09d", konec);

        HttpAsyncTask2 myHttpAsyncTask2 = new HttpAsyncTask2();
        myHttpAsyncTask2.execute( serverConfigDTO.getXServerURL() + "?op=present&set_entry="+ entryZacatek +"-"+ entryKonec +"&set_number="+ kodHledani +"&format=marc");

        try {
            myHttpAsyncTask2.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) {
        builder = new StringBuilder();

        if (elementName.equalsIgnoreCase("record")) {
            predmety = new ArrayList<>();
            poznamky = new ArrayList<>();
            presentDTO = new PresentDTO();
        }

        if (elementName.equalsIgnoreCase("varfield")) {
            varfieldID = attributes.getValue("id");
        }
        if (elementName.equalsIgnoreCase("subfield")) {
            subfieldLabel = attributes.getValue("label");
        }
    }
    @Override
    public void endElement(String s, String s1, String element) {
        if (element.equals("record")) {
            presentDTO.setPoznamka(poznamky);
            presentDTO.setPredmet(predmety);
            presentDTOs.add(presentDTO);
        }

        if (element.equals("doc_number")) {
            presentDTO.setDoc_number(tmpValue);
        }

        if (element.equalsIgnoreCase("subfield")) {

            if (varfieldID.equals("020")&&subfieldLabel.equals("a")){
                if (tmpValue.indexOf(" ")>0) {
                    presentDTO.setIsbn(tmpValue.substring(0,tmpValue.indexOf(" ")).replace(" ","").replace(".",""));
                } else {
                    presentDTO.setIsbn(tmpValue.replace(" ","").replace(".",""));
                }
                presentDTO.setObalka(presentDTO.getIsbn());
            }
            if (varfieldID.equals("040") && subfieldLabel.equals("b")){
                presentDTO.setJazyk(languageDeCode(tmpValue));
            }
            if (varfieldID.equals("245") && subfieldLabel.equals("a")){
                if (tmpValue.endsWith("/")){
                    presentDTO.setNazev(tmpValue.substring(0, tmpValue.length()-1));
                } else {
                    presentDTO.setNazev(tmpValue);
                }
            }
            if (varfieldID.equals("245")&&subfieldLabel.equals("b")){
                //presentDTO.setPodNazev(tmpValue);
                if (tmpValue.endsWith("/")){
                    presentDTO.setPodNazev(tmpValue.substring(0, tmpValue.length()-1));
                } else {
                    presentDTO.setPodNazev(tmpValue);
                }
            }
            if (varfieldID.equals("100") && subfieldLabel.equals("a")){
                presentDTO.setAutor(tmpValue);
            }
            if (varfieldID.equals("260") && subfieldLabel.equals("a")){
                presentDTO.setImprint(tmpValue);
            }
            if (varfieldID.equals("260") && subfieldLabel.equals("b")){
                presentDTO.setImprint(presentDTO.getImprint()+tmpValue);
            }
            if (varfieldID.equals("260") && subfieldLabel.equals("c")){
                presentDTO.setImprint(presentDTO.getImprint()+tmpValue);
                presentDTO.setImprintRok(tmpValue);
            }
            if (varfieldID.equals("250") && subfieldLabel.equals("a")){
                presentDTO.setVydani(tmpValue);
            }
            if (varfieldID.equals("300") && subfieldLabel.equals("a")){
                presentDTO.setPopis(tmpValue);
            }
            if (varfieldID.equals("300") && subfieldLabel.equals("b")){
                presentDTO.setPopis(presentDTO.getPopis()+tmpValue);
            }
            if (varfieldID.equals("500") && subfieldLabel.equals("a")){
                poznamky.add(tmpValue);
            }
            if (varfieldID.startsWith("6") && subfieldLabel.equals("a")){
                predmety.add(tmpValue);
            }
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) {
        //  tmpValue = new String(ac, i, j);
        builder.append(ac, i, j);
        tmpValue = builder.toString();
    }

    public List<PresentDTO> getDTOs() {
        return presentDTOs;
    }

    protected String getParserResult() {
        return getDTOs().toString();
    }

    public FindDTO getFindDTO() {
        return findDTO;
    }

    public String languageDeCode(String languageCode) {
/*
        if (ContextBridge.context.getResources().getIdentifier(tmpValue, "string", ContextBridge.context.getPackageName())==0){
            return languageCode;
        }
        else {
            return ContextBridge.context.getString(ContextBridge.context.getResources().getIdentifier(tmpValue, "string", ContextBridge.context.getPackageName()));
        }  */
//TODO: vyresit
        return "";
    }

    public Boolean existObalka(String isbn) {

        File file = new File(MainActivity.IMAGECACHEDIR, isbn + ".png");
        return !file.exists();
    }


    public void saveImageToDiskCache(String obalka) {

        if(!MainActivity.IMAGECACHEDIR.exists()) MainActivity.IMAGECACHEDIR.mkdirs();

        File file = new File(MainActivity.IMAGECACHEDIR, obalka + ".png");
        if(!file.exists() && image.getByteCount()>1) {

            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(file);

                image.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return parseData(urls[0]);
        }

    }


}
