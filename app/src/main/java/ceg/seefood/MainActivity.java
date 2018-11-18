package ceg.seefood;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Global buttons
    private Button _uploadBtn, _galleryBtn, _aboutBtn;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int FILE_SELECT_CODE = 0;
    String mCurrentPhotoPath;
    Uri photoURI;

    //ImageView tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize buttons
        initializeButtons();
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
                //  Show options || go directly to gallry w/ camera.
                showUploadOptions();
                break;
            case R.id.gallerybtn:
                launchGallery();
                break;
            case R.id.aboutbtn:
                // Show infromation about product.
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
        String[] options = {"Take a photo", "Import from device"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload image(s) from ...");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){
                    // If user chooses camera, then open camera.
                    openDeviceCamera();
                } else {
                    // If user chooses import from device, then open their storage. [1]
                    openImageChooser();
                }
            }
        });
        builder.show();
    }
    // Opens the image chooser.
    private void openImageChooser(){
        Intent storageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        storageIntent.setType("image/*");
        // Enable selecting multiple images form gallery
        storageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent chooser = Intent.createChooser(storageIntent, "Choose an image");
        startActivityForResult(chooser, FILE_SELECT_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){


        ArrayList<Uri> imagUris = new ArrayList<Uri>();
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE && data != null){

            // Check if multiple images have been selected
            if (data.getClipData() != null){
                Toast.makeText(this, "Multiple! " + data.getClipData().getItemCount(), Toast.LENGTH_LONG).show();

                // Send selected images to the feedback view.
                for (int i = 0; i < data.getClipData().getItemCount(); i++){
                    // display image sequentially to the user.
                    moveToFeedbackWindow(data.getClipData().getItemAt(i).getUri());
                }

            } else {
                // A single image was selected.
                Toast.makeText(this, "Single!", Toast.LENGTH_LONG).show();
                // Send the single image to the feedback view
                moveToFeedbackWindow(data.getData());
            }
        }
        // If image is taken by the Camera
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
            Toast.makeText(this, "Camera!", Toast.LENGTH_LONG).show();
            // send the captured image for feedback window.
            moveToFeedbackWindow(photoURI);
        }

    }

    private void moveToFeedbackWindow(Uri selectedImageUri){
        Intent feedbackIntent = new Intent(MainActivity.this, Feedback_Window.class);
        // Send image uri to feedback
        feedbackIntent.putExtra("ImageUri", selectedImageUri.toString());
        startActivity(feedbackIntent);
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
                // Store captured image URI.
                 photoURI = FileProvider.getUriForFile(this,
                        "com.example.android fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

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
    // opens the gallery for users.
    private void launchGallery(){
        Intent intent = new Intent(this, Gallery.class);
        startActivity(intent);
    }
}
