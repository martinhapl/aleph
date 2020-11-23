package net.hapl.aleph.factory;

import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public abstract class AbstractXmlParseDefaultHandler extends DefaultHandler {

    protected abstract String getParserResult();

    protected String parseData(String urlLink) {
        Log.d(this.getClass().getName(), "url: " + urlLink);
        String result = null;

        URL url;
        HttpURLConnection urlConnection = null;
        InputStream inputStream;

        try {
            url = new URL(urlLink);
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                parser.parse(inputStream, this);
            } catch (ParserConfigurationException e) {
                System.out.println("ParserConfig error");
            } catch (SAXException e) {
                System.out.println("SAXException : xml not well formed");
            } catch (IOException e) {
                System.out.println("IO error");
            }

            if (inputStream != null) {
                result = getParserResult();
            } else {
                result = "Did not work!";
                Log.d(this.getClass().getName(), "result: " + result);
            }


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

}
