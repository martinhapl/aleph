package net.hapl.aleph.factory;

import android.os.AsyncTask;

import net.hapl.aleph.model.ItemDataDTO;
import net.hapl.aleph.model.PresentDTO;
import net.hapl.aleph.model.ServerConfigDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemDataXMLParser extends AbstractXmlParseDefaultHandler {

    private final String doc_number;
    private String dueDate;
    private String barcode;
    private String iStatus;
    private String onHold;
    private String requested;
    private String expected;
    private String subLibrary;
    private String collection;
    private String location;
    private String location2;
    private String description;
    private String tmpValue;
    private final List<ItemDataDTO> itemDataDTOs;

    public ItemDataXMLParser(ServerConfigDTO serverConfigDTO, PresentDTO document) {

        doc_number = document.getDoc_number();
        dueDate = null;
        barcode = null;
        iStatus = null;
        onHold = null;
        requested = null;
        expected = null;
        subLibrary = null;
        collection = null;
        location = null;
        location2 = null;
        description = null;
        itemDataDTOs = new ArrayList<>();

        HttpAsyncTaskRenew myHttpAsyncTaskRenew = new HttpAsyncTaskRenew();
        myHttpAsyncTaskRenew.execute(serverConfigDTO.getXServerURL() + "?op=circ-status&sys_no=" + document.getDoc_number() + "&library=" + serverConfigDTO.getBaseBIB() + "&user_name=" + serverConfigDTO.getUser() + "&user_password=" + serverConfigDTO.getUserPassword());
        try {
            myHttpAsyncTaskRenew.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String s, String s1, String element)  {

        if (element.equalsIgnoreCase("item-data")) {
            itemDataDTOs.add(
                    new ItemDataDTO(doc_number, dueDate, barcode, iStatus, onHold, requested,
                            expected, subLibrary, collection, location, location2, description));
        }
        if (element.equalsIgnoreCase("due-date")) {
            dueDate = tmpValue;
            if (tmpValue.equalsIgnoreCase("On Shelf")) {
                dueDate = "Na místě";
            }
            if (tmpValue.equalsIgnoreCase("Requested")) {
                dueDate = "Požadováno";
            }
        }
        if (element.equalsIgnoreCase("barcode")) {
            barcode = tmpValue;
        }
        if (element.equalsIgnoreCase("loan-status")) {
            iStatus = tmpValue;
            if (tmpValue.equalsIgnoreCase("Open Stack")) {
                iStatus = "Volný výběr";
            }
            if (tmpValue.equalsIgnoreCase("Regular loan")) {
                iStatus = "Absenčně - 1 měsíc";
            }
            if (tmpValue.equalsIgnoreCase("Study Loan")) {
                iStatus = "Studovna";
            }
        }
        if (element.equalsIgnoreCase("on-hold")) {
            onHold = tmpValue;
        }
        if (element.equalsIgnoreCase("requested")) {
            requested = tmpValue;
        }
        if (element.equalsIgnoreCase("expected")) {
            expected = tmpValue;
        }
        if (element.equalsIgnoreCase("sub-library")) {
            subLibrary = tmpValue;
        }
        if (element.equalsIgnoreCase("collection")) {
            collection = tmpValue;
            collection = collection.replace("Open Stack-","");
            collection = collection.replace("Floor"," NP");
            if (tmpValue.equalsIgnoreCase("Main Stack-45 min.")) {
                collection = "Sklad - do 45 min.";
            }
        }
        if (element.equalsIgnoreCase("location")) {
            location = tmpValue;
        }
        if (element.equalsIgnoreCase("location-2")) {
            location2 = tmpValue;
        }
        if (element.equalsIgnoreCase("z30-description")) {
            description = tmpValue;
        }
    }
    @Override
    public void characters(char[] ac, int i, int j)  {
        tmpValue = new String(ac, i, j);
    }

    public List<ItemDataDTO> getItemDataDTOs() {
        return itemDataDTOs;
    }

    protected String getParserResult() {
        return "Spojeni probehlo!";
    }

    private class HttpAsyncTaskRenew extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
    }
}
