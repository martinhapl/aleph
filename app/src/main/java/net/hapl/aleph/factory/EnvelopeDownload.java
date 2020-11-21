package net.hapl.aleph.factory;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.control.MainActivityComm;
import net.hapl.aleph.model.PresentDTO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class EnvelopeDownload extends AsyncTask<List<PresentDTO>, String, String> {

    private static final String TAG = "ObalkaDownload";

    private final Context context;
    private final MainActivityComm comm;
    private List<PresentDTO> presentDTOs;

    public EnvelopeDownload(Context context) {
        this.context = context;
        comm = (MainActivityComm) context;
    }

    protected Bitmap downloadBitmap(String urlLink) throws IOException {
        URL url;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            url = new URL(urlLink);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();

                byte[] bytes = readBytes(inputStream);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                return bitmap;
            }  else {
                throw new IOException("Download failed, HTTP response code "
                        + responseCode);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected String doInBackground(List<PresentDTO>... params) {
        // stahovani obalek a ukladani do chache
        // melo by se to ulozit tak, aby v SearchListAdapteru stacilo
        // zavolat nactiObalku() a metoda vratila tu obalku

        // pokud teda uz ta obalka je v cachi, tak aby se nestahovala znovu

        // pokud uz se nekdy stahovala a zadnou to nevratilo,
        // tak by se taky nemela stahovat znovu..nebo jo? treba tam uz bude
        presentDTOs = params[0];
        for (int j=0; j<presentDTOs.size(); j++) {
            Log.d(TAG, "Obalka: " + presentDTOs.get(j).getObalka());
            if (presentDTOs.get(j).getObalka() != null) {
                if (existObalkaInCache(presentDTOs.get(j).getObalka())) {

                    Bitmap tempObalka = null;
                    try {
                        tempObalka = downloadBitmap("https://www.obalkyknih.cz/api/cover?isbn="+ presentDTOs.get(j).getObalka());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(tempObalka.getByteCount() > 1) {
                        saveImageToDiskCache(presentDTOs.get(j).getObalka(), tempObalka);
                    }
                    else {
                        presentDTOs.get(j).setObalka(null);
                    }
                }
            }
        }
        return "ok";
    }

    public Boolean existObalkaInCache(String isbn) {

        File file = new File(MainActivity.IMAGECACHEDIR, isbn + ".png");
        return !file.exists();
    }

    public void saveImageToDiskCache(String obalka, Bitmap ukladanaObalka) {
        File file = new File(MainActivity.IMAGECACHEDIR, obalka + ".png");
        if(!file.exists() && ukladanaObalka.getByteCount() > 1) {

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

    @Override
    protected void onPostExecute(String result) {
        // update list adapteru pres aktivitu
        // update delat jenom pokud se stahne, jinak neni potreba

        comm.updateSearchAdapter();
    }

    private class HttpAsyncTaskOb extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap tempBitmap=null;
            try {
                tempBitmap = downloadBitmap(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  tempBitmap;
        }
    }
}
