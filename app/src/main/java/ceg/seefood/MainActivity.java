package ceg.seefood;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Global buttons
    private Button _uploadBtn, _galleryBtn, _aboutBtn;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;

    GalleryAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<GalleryItem> images = new ArrayList<>();
    public String IMGS[] = {
            "https://images.unsplash.com/photo-1444090542259-0af8fa96557e?q=80&fm=jpg&w=1080&fit=max&s=4b703b77b42e067f949d14581f35019b"
    };

    //ImageView tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize buttons
        initializeButtons();
//        tmp  = (ImageView) findViewById(R.id.imageView);


    }

    private void initializeButtons(){
        // initialize buttons
        _uploadBtn = (Button) findViewById(R.id.uploadbtn);
        _galleryBtn = (Button) findViewById(R.id.gallerybtn);
        _aboutBtn = (Button) findViewById(R.id.aboutbtn);
        // Set listener for all buttons
        _uploadBtn.setOnClickListener(this);
        _galleryBtn.setOnClickListener(this);
        _aboutBtn.setOnClickListener(this);
    }

    // Handling buttons clicks
    @Override
    public void onClick(View v) {

        // Determine clicked button
        switch(v.getId()){
            case R.id.uploadbtn:
                // TODO: Show options || go directly to gallry w/ camera.
                showUploadOptions();
                break;
            case R.id.gallerybtn:
                openGallery();
                break;
            case R.id.aboutbtn:
                // TODO: Show infromation about product.
                AlertDialog infoDialog = new AlertDialog.Builder(this).create();
                infoDialog.setTitle("About");
                infoDialog.setMessage("Version 1.0.0\nThis is CEG4110 group project\nContact at: unknown@example.com\nFAQ:\n" +
                        "LinktoFrequentQustions.com");
                infoDialog.show();
                break;
        }
    }
    // show uploading options after clicking the upload button
    private void showUploadOptions(){
        String[] options = {"Camera", "Import from device"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload image(s) from ...");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){
                    // TODO: check permissions
                    // If user chooses camera, then open camera.
                    openDeviceCamera();
                } else {
                    // TODO: If user chooses import from device, then open their storage. [1]
                }
            }
        });
        builder.show();
    }

    // Opens the device's camera when users choose to use camera.
    private void openDeviceCamera(){
        // Create new intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // more information will be added once i understand this! :)
        if (cameraIntent.resolveActivity(getPackageManager())!= null){
            // create file for image
            File photoFile = null;
            try {
                // call createImageFile to create the photo file
                photoFile = createImageFile();
            } catch (IOException ex){
                // error: file cannot be create
            }

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

                // TODO: Hold a reference of the image inside a bitmap variable.
                /*Bitmap mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                tmp.setImageBitmap(mBitmap);*/
            }


        }

    }
    // Create new image file
    private File createImageFile() throws IOException {
        // Create in image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openGallery(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View galleryView = inflater.inflate(R.layout.gallery_layout,null);
        builder.setView(galleryView);

        for(int i = 0; i < IMGS.length; i++){
            GalleryItem item = new GalleryItem();
            item.setName("Image " + i);
            item.setUrl(IMGS[i]);
            images.add(item);
        }

        recyclerView = (RecyclerView)galleryView.findViewById(R.id.gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setHasFixedSize(true);

        adapter = new GalleryAdapter(MainActivity.this,images);
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        TextView title = new TextView(this);
        title.setText("Gallery");
        title.setPadding(10,10,10,10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        dialog.setCustomTitle(title);

        new Dialog(getApplicationContext());

        dialog.show();
    }
}
