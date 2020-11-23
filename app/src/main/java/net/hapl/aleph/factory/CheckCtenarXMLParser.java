package net.hapl.aleph.factory;

import android.os.AsyncTask;

import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.ServerConfigDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class CheckCtenarXMLParser extends AbstractXmlParseDefaultHandler {

    private String errorAuth;
    private String hesloCtenar;
    private String loginCtenar;
    private String tmpValue;

    public CheckCtenarXMLParser(UserConfigDTO userConfigDTO, ServerConfigDTO serverConfigDTO) {

        errorAuth = null;

        if (!(userConfigDTO.getLogin() == null || userConfigDTO.getPasswd() == null)) {
            try {
                loginCtenar = URLEncoder.encode(userConfigDTO.getLogin(), "UTF-8");
                hesloCtenar = URLEncoder.encode(userConfigDTO.getPasswd(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpAsyncTaskCtenar myHttpAsyncTaskCtenar = new HttpAsyncTaskCtenar();
            myHttpAsyncTaskCtenar.execute(serverConfigDTO.getXServerURL() + "?op=bor-auth&bor_id=" + loginCtenar + "&verification=" + hesloCtenar + "&library=" + serverConfigDTO.getBaseADM() + "&user_name=" + serverConfigDTO.getUser() + "&user_password=" + serverConfigDTO.getUserPassword());
            try {
                myHttpAsyncTaskCtenar.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        else {
            errorAuth = "Není zadáno uživatelské jeméno nebo heslo";
        }
    }

    @Override
    public void endElement(String s, String s1, String element) {

        if (element.equalsIgnoreCase("error")) {
            errorAuth = tmpValue;
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) {
        tmpValue = new String(ac, i, j);
    }

    public String getErrorAuth() {
        return errorAuth;
    }

    protected String getParserResult() {
        return "Spojeni probehlo!";
    }


    private class HttpAsyncTaskCtenar extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return parseData(urls[0]);
        }
    }
}
