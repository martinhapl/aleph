package net.hapl.aleph.factory;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.CtenarDTO;
import net.hapl.aleph.model.RezervaceDTO;
import net.hapl.aleph.model.ServerConfigDTO;
import net.hapl.aleph.model.VypujckaDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.xml.sax.Attributes;


public class CtenarXMLParser extends AbstractXmlParseDefaultHandler {

    private String name;
    private String email;
    private String ctenarID;
    private String date_from;
    private String date_to;

    private String docNumber;
    private String itemSequence;
    private String barcode;
    private String material;
    private String subLibrary;
    private String status;
    private String loanDate;
    private String loanHour;
    private String dueDate;
    private String dueHour;
    private String autor;
    private String nazev;
    private String imprint;
    private String vydani;
    private String isbn;
    private String predmet;
    private String requestDate;
    private String endRequestDate;
    private String sequence;

    private String tmpValue;
    private final List<CtenarDTO> ctenarDTOList;
    private final List<RezervaceDTO> rezervaceDTOList;
    private final List<VypujckaDTO> vypujckaDTOList;
    private String loginCtenar;
    private String hesloCtenar;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public CtenarXMLParser(UserConfigDTO userConfigDTO, ServerConfigDTO serverConfigDTO) {

        ctenarDTOList = new ArrayList<>();
        rezervaceDTOList = new ArrayList<>();
        vypujckaDTOList = new ArrayList<>();

        if (!(userConfigDTO.getLogin()==null|| userConfigDTO.getPasswd()==null)) {
            try {
                loginCtenar = URLEncoder.encode(userConfigDTO.getLogin(), "UTF-8");
                hesloCtenar = URLEncoder.encode(userConfigDTO.getPasswd(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            loginCtenar = "";
            hesloCtenar = "";
        }

        HttpAsyncTaskCtenar myHttpAsyncTaskCtenar = new HttpAsyncTaskCtenar();
        myHttpAsyncTaskCtenar.execute(serverConfigDTO.getXServerURL() + "?op=bor-info&bor_id=" + loginCtenar + "&verification=" + hesloCtenar + "&library=" + serverConfigDTO.getBaseADM() + "&user_name=" + serverConfigDTO.getUser() + "&user_password=" + serverConfigDTO.getUserPassword());
        try {
            myHttpAsyncTaskCtenar.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) {
        String tagItem;
        if (elementName.equalsIgnoreCase("item-h")) {
            tagItem = "item-h";
            this.docNumber = null;
            this.itemSequence = null;
            this.barcode = null;
            this.material = null;
            this.subLibrary = null;
            this.status = null;
            this.loanDate = null;
            this.loanHour = null;
            this.dueDate = null;
            this.dueHour = null;
            this.autor = null;
            this.nazev = null;
            this.imprint = null;
            this.vydani = null;
            this.isbn = null;
            this.predmet = null;
            this.requestDate = null;
            this.endRequestDate = null;
            this.sequence = null;
        }
        if (elementName.equalsIgnoreCase("item-l")) {
            tagItem = "item-l";
            this.docNumber = null;
            this.itemSequence = null;
            this.barcode = null;
            this.material = null;
            this.subLibrary = null;
            this.status = null;
            this.loanDate = null;
            this.loanHour = null;
            this.dueDate = null;
            this.dueHour = null;
            this.autor = null;
            this.nazev = null;
            this.imprint = null;
            this.vydani = null;
            this.isbn = null;
            this.predmet = null;
            this.requestDate = null;
            this.endRequestDate = null;
            this.sequence = null;
        }
    }
    @Override
    public void endElement(String s, String s1, String element) {

        if (element.equals("bor-info")) {
            ctenarDTOList.add(new CtenarDTO(name, email, ctenarID, date_from, date_to, rezervaceDTOList, vypujckaDTOList));
        }
        if (element.equals("item-h")) {
            rezervaceDTOList.add(new RezervaceDTO(docNumber, itemSequence, barcode, material, subLibrary,
                    status, requestDate, endRequestDate, autor, nazev,
                    imprint, vydani, isbn, predmet, sequence));
        }
        if (element.equals("item-l")) {
            vypujckaDTOList.add(new VypujckaDTO(docNumber, itemSequence, barcode, material,
                    subLibrary, status, loanDate, loanHour, dueDate,
                    dueHour, autor, nazev, imprint, vydani, isbn, predmet));
        }
        if (element.equalsIgnoreCase("z304-address-0")) {
            name = tmpValue;
        }
        if (element.equalsIgnoreCase("z304-email-address")) {
            email = tmpValue;
        }
        if (element.equalsIgnoreCase("z304-id")) {
            ctenarID = tmpValue;
        }
        if (element.equalsIgnoreCase("z305-update-date")) {
            date_from = tmpValue;
        }
        if (element.equalsIgnoreCase("z305-expiry-date")) {
            date_to = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-doc-number")) {
            docNumber = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-item-sequence")) {
            itemSequence = tmpValue;
        }
        if (element.equalsIgnoreCase("z30-barcode")) {
            barcode = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-material")) {
            material = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-sub-library")) {
            subLibrary = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-status")) {
            status = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-loan-date")) {
            loanDate = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-loan-hour")) {
            loanHour = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-due-date")) {
            dueDate = tmpValue;
        }
        if (element.equalsIgnoreCase("z36-due-hour")) {
            dueHour = tmpValue;
        }
        if (element.equalsIgnoreCase("z13-author")) {
            autor = tmpValue;
        }
        if (element.equalsIgnoreCase("z13-title")) {
            nazev = tmpValue;
        }
        if (element.equalsIgnoreCase("z13-imprint")) {
            imprint = tmpValue;
        }
        if (element.equalsIgnoreCase("z13-year")) {
            vydani = tmpValue;
        }
        if (element.equalsIgnoreCase("z13-isbn-issn")) {
            isbn = tmpValue;
        }
        if (element.equalsIgnoreCase("z30-predmet")) {
            predmet = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-doc-number")) {
            docNumber = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-item-sequence")) {
            itemSequence = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-barcode")) {
            barcode = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-material")) {
            material = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-sub-library")) {
            subLibrary = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-status")) {
            status = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-request-date")) {
            requestDate = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-end-request-date")) {
            endRequestDate = tmpValue;
        }
        if (element.equalsIgnoreCase("z37-sequence")) {
            sequence = tmpValue;
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) {
        tmpValue = new String(ac, i, j);
    }

    public List<CtenarDTO> getDTOs() {
        return ctenarDTOList;
    }

    protected String getParserResult() {
        return getDTOs().toString();
    }


    private class HttpAsyncTaskCtenar extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return parseData(urls[0]);
        }
    }
}
