package ceg.seefood;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/*
* This class extends AsyncTask in order to download the gallery from the server as a zip file.
*/

class DownloadFileAsync extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... aurl){
        int count;

        try{
            URL url = new URL(aurl[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            int length = connection.getContentLength();
            Log.d("ANDRO_ASYNC", "Length of file: " + length);

            InputStream inputStream = new BufferedInputStream(url.openStream());
            OutputStream outputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/thumbs.zip");

            byte data[] = new byte[1024];
            long total = 0;

            while((count = inputStream.read(data)) != -1){
                total += count;
                publishProgress(""+(int)((total*100)/length));
                outputStream.write(data,0,count);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch(Exception e){}
        return null;
    }
}