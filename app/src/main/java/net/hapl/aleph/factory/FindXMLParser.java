package net.hapl.aleph.factory;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import net.hapl.aleph.model.FindDTO;
import net.hapl.aleph.model.ServerConfigDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.xml.sax.Attributes;


public class FindXMLParser extends AbstractXmlParseDefaultHandler {

    private static final String TAG = "FindXMLParser";

    private String tmpValue;
    private FindDTO FindQTmp;
    private final List<FindDTO> FindQList;

    public FindXMLParser(String hledaneSlovo, ServerConfigDTO serverConfigDTO) {
        FindQList = new ArrayList<>();

        Looper.prepare();
        HttpAsyncTaskFind myHttpAsyncTaskFind = new HttpAsyncTaskFind();

        try {
            hledaneSlovo = URLEncoder.encode(hledaneSlovo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = serverConfigDTO.getXServerURL() + "?op=find&base="+serverConfigDTO.getBaseBIB()+"&request="+hledaneSlovo;
        Log.d(TAG, "url: " + url);
        myHttpAsyncTaskFind.execute(url);
        // myHttpAsyncTask.execute(serverConfigDTO.getXServerURL() + "?op=find&base="+serverConfigDTO.getBaseBIB()+"&request="+hledaneSlovo.replace(" ","="));
        try {
            myHttpAsyncTaskFind.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) {
        if (elementName.equalsIgnoreCase("find")) {
            FindQTmp = new FindDTO();
        }
    }
    @Override
    public void endElement(String s, String s1, String element) {
        if (element.equals("find")) {
            FindQList.add(FindQTmp);
        }
        if (element.equalsIgnoreCase("set_number")) {
            FindQTmp.setSet_number(tmpValue);
        }
        if (element.equalsIgnoreCase("no_records")) {
            FindQTmp.setNo_record(tmpValue);
        }
        if(element.equalsIgnoreCase("no_entries")){
            FindQTmp.setNo_entries(tmpValue);
        }
        if(element.equalsIgnoreCase("session-id")){
            FindQTmp.setSession_id(tmpValue);
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) {
        tmpValue = new String(ac, i, j);
    }

    public List<FindDTO> getDTOs() {
        return FindQList;
    }

    protected String getParserResult() {
        return getDTOs().toString();
    }

    private class HttpAsyncTaskFind extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return parseData(urls[0]);
        }
    }
}
