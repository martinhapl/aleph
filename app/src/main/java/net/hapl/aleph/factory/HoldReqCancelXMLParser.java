package net.hapl.aleph.factory;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.RezervaceDTO;
import net.hapl.aleph.model.ServerConfigDTO;

import java.util.concurrent.ExecutionException;

public class HoldReqCancelXMLParser extends AbstractXmlParseDefaultHandler {

    String errorHoldReqCancel;
    String reply;
    String tmpValue;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public HoldReqCancelXMLParser(UserConfigDTO userConfigDTO, ServerConfigDTO serverConfigDTO, RezervaceDTO holdItem) {

        errorHoldReqCancel = null;
        reply = null;

        HttpAsyncTaskHoldReqCancel myHttpAsyncTaskHoldReqCancel = new HttpAsyncTaskHoldReqCancel();
        myHttpAsyncTaskHoldReqCancel.execute(serverConfigDTO.getXServerURL() + "?op=hold-req-cancel&doc_number=" + holdItem.getDocNumber() + "&item_sequence=" + holdItem.getItemSequence() + "&sequence=" + holdItem.getSequence() + "&library=" + serverConfigDTO.getBaseADM() + "&user_name=" + serverConfigDTO.getUser() + "&user_password=" + serverConfigDTO.getUserPassword());
        try {
            myHttpAsyncTaskHoldReqCancel.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String s, String s1, String element) {

//        if (element.equalsIgnoreCase("hold-req")) {
//            new RenewDTO = tmpValue;
//        }
        if (element.equalsIgnoreCase("error")) {
            errorHoldReqCancel = tmpValue;
        }
        if (element.equalsIgnoreCase("reply")) {
            reply = tmpValue;
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) {
        tmpValue = new String(ac, i, j);
    }

    public String getErrorHoldReqCancel() {
        return errorHoldReqCancel;
    }

    public String getReply() {
        return reply;
    }

    protected String getParserResult() {
        return "Spojeni probehlo!";
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class HttpAsyncTaskHoldReqCancel extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
    }
}