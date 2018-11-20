package ceg.seefood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Gallery extends AppCompatActivity {

    GalleryAdapter adapter;
    RecyclerView recyclerView;
    String path;
    String folderName;
    String zipName;
    File gallery;
    File downloads;
    File pics[];

    ArrayList<GalleryItem> images = new ArrayList<>();

    public List<String> IMGS = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Paths for all of the necessary locations
        zipName = "/thumbs.zip";
        downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        path = downloads.getAbsolutePath();
        folderName = path + zipName;
        gallery = new File(path + "/imgs/thumbnails");

        // Download the gallery folder if it isn't currently stored on the device
        if(!(new File(folderName).exists())) {
            downloadGallery();
        }

        // Checks file permissions and unzips the folder if granted. The zip file is the deleted
        if(ActivityCompat.checkSelfPermission(
                Gallery.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Gallery.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);

            deleteZip(gallery.getAbsolutePath());
            unzip(folderName);
            deleteZip(folderName);

        }else{
            deleteZip(gallery.getAbsolutePath());
            unzip(folderName);
            deleteZip(folderName);
        }

        // Copies the name of each file into a list of strings
        if (gallery.isDirectory()) {
            pics = gallery.listFiles();
            for (int i = 0; i < pics.length; i++) {
                IMGS.add(pics[i].getPath());
            }
        }

        // Creates a gallery item for each filename of the gallery
        for(int i = 0; i < IMGS.size(); i++){
            GalleryItem item = new GalleryItem();
            item.setName("Image " + i);
            item.setUrl(IMGS.get(i));
            images.add(item);
        }

        // Puts the gallery items into a view
        recyclerView = (RecyclerView)findViewById(R.id.gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setHasFixedSize(true);

        adapter = new GalleryAdapter(Gallery.this, images);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new ImageClickListener(this,
                new ImageClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Gallery.this, DetailActivity.class);
                        intent.putParcelableArrayListExtra("images", images);
                        intent.putExtra("pos", position);
                        startActivity(intent);
                    }
                }));
    }

    // Unzips the gallery folder
    public static boolean unzip(String source){

        InputStream inputStream;
        ZipInputStream zipInputStream;

        try{
            File zipFile = new File(source);
            String parent = zipFile.getParentFile().getPath();
            String filename;

            inputStream = new FileInputStream(source);
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry;
            byte[] buffer = new byte[4096];
            int count;

            // Loops through each image in the folder and writes them to the new folder
            while((zipEntry = zipInputStream.getNextEntry()) != null){
                filename = zipEntry.getName();

                // Create the final destination directory if it doesn't exist
                if(!(new File(parent + "/imgs/thumbnails")).isDirectory()){
                    File fileDir = new File(parent + "/imgs/thumbnails");
                    fileDir.mkdirs();
                }

                FileOutputStream fileOutputStream = new FileOutputStream(parent + "/" + filename);

                // Writes the file to the unzipped folder destination
                while((count = zipInputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer,0,count);
                }

                fileOutputStream.close();
                zipInputStream.closeEntry();
            }

            zipInputStream.close();
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // Downloads the gallery as a zipped folder from the SeeFood server
    private void downloadGallery (){
        try {
            String url = "http://seefood.moostermiko.com:80/thumbnails";
            new DownloadFileAsync().execute(url).get();
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Deletes the zipped folder that was downloaded so that the gallery is always up-to-date
    // whenever it is being viewed
    public void deleteZip(String zipFile){
        File file = new File(zipFile);
        if(file.exists()){
            file.delete();
        }

        if(file.isDirectory()){
            File folder = new File(zipFile);

            try {
                FileUtils.deleteDirectory(folder);

            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
