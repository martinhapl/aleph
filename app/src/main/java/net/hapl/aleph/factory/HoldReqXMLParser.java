package net.hapl.aleph.factory;

import android.os.AsyncTask;
import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.ItemDataDTO;
import net.hapl.aleph.model.ServerConfigDTO;

import java.util.concurrent.ExecutionException;

public class HoldReqXMLParser extends AbstractXmlParseDefaultHandler {

    private String errorHoldReq;
    private String reply;
    private String tmpValue;

    public HoldReqXMLParser(UserConfigDTO userConfigDTO, ServerConfigDTO serverConfigDTO, ItemDataDTO holdItem) {

        errorHoldReq = null;
        reply = null;

        HttpAsyncTaskHoldReq myHttpAsyncTaskHoldReq = new HttpAsyncTaskHoldReq();
        myHttpAsyncTaskHoldReq.execute(serverConfigDTO.getXServerURL() + "?op=hold-req&bor_id=" + userConfigDTO.getLogin() + "&item_barcode=" + holdItem.getBarcode() + "&library=" + serverConfigDTO.getBaseADM() + "&user_name=" + serverConfigDTO.getUser() + "&user_password=" + serverConfigDTO.getUserPassword());
        try {
            myHttpAsyncTaskHoldReq.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String s, String s1, String element) {

//        if (element.equalsIgnoreCase("hold-req")) {
//            new RenewDTO = tmpValue;
//        }
        if (element.equalsIgnoreCase("error")) {
            errorHoldReq = tmpValue;
        }
        if (element.equalsIgnoreCase("reply")) {
            reply = tmpValue;
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) {
        tmpValue = new String(ac, i, j);
    }

    public String getErrorHoldReq() {
        return errorHoldReq;
    }

    public String getReply() {
        return reply;
    }

    protected String getParserResult() {
        return "Spojeni probehlo!";
    }

    private class HttpAsyncTaskHoldReq extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return parseData(urls[0]);
        }
    }
}