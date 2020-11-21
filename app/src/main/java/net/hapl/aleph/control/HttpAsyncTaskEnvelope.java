package net.hapl.aleph.control;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import net.hapl.aleph.factory.HttpFactory;

import java.io.IOException;

public class HttpAsyncTaskEnvelope extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap tempBitmap=null;
        try {
            tempBitmap = HttpFactory.downloadBitmap(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  tempBitmap;
    }
}