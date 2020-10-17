package net.hapl.aleph.factory;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.ServerConfigDTO;
import net.hapl.aleph.model.VypujckaDTO;

import java.util.concurrent.ExecutionException;

public class RenewXMLParser extends AbstractXmlParseDefaultHandler {

    private String errorRenew;
    private String reply;
    private String dueHour;
    private String dueDate;
    private String tmpValue;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public RenewXMLParser(UserConfigDTO userConfigDTO, ServerConfigDTO serverConfigDTO, VypujckaDTO pujcenaKniha) {

        errorRenew = null;
        reply = null;
        dueHour = null;
        dueDate = null;

        HttpAsyncTaskRenew myHttpAsyncTaskRenew = new HttpAsyncTaskRenew();
        myHttpAsyncTaskRenew.execute(serverConfigDTO.getXServerURL() + "?op=renew&bor_id=" + userConfigDTO.getLogin() + "&item_barcode=" + pujcenaKniha.getBarcode() + "&library=" + serverConfigDTO.getBaseADM() + "&user_name=" + serverConfigDTO.getUser() + "&user_password=" + serverConfigDTO.getUserPassword());
        try {
            myHttpAsyncTaskRenew.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String s, String s1, String element) {

//        if (element.equalsIgnoreCase("renew")) {
//            new RenewDTO = tmpValue;
//        }
        if (element.equalsIgnoreCase("error")) {
            errorRenew = tmpValue;
        }
        if (element.equalsIgnoreCase("reply")) {
            reply = tmpValue;
        }
        if (element.equalsIgnoreCase("due-date")) {
            dueDate = tmpValue;
        }
        if (element.equalsIgnoreCase("due-hour")) {
            dueHour = tmpValue;
        }

    }
    @Override
    public void characters(char[] ac, int i, int j) {
        tmpValue = new String(ac, i, j);
    }

    public String getErrorRenew() {
        return errorRenew;
    }

    public String getReply() {
        return reply;
    }

    protected String getParserResult() {
        return "Spojeni probehlo!";
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class HttpAsyncTaskRenew extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
    }
}