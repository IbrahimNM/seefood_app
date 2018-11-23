package ceg.seefood;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int FILE_SELECT_CODE = 0;
    // Global variables
    private Button _uploadBtn, _galleryBtn, _aboutBtn;
    private Uri photoURI;


    /**
     * MainActivity view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize buttons
        initializeButtons();
    }

    /**
     * Initializes the buttons by linking 'em to the layout buttons.
     */
    private void initializeButtons() {
        // initialize buttons
        _uploadBtn = findViewById(R.id.uploadbtn);
        _galleryBtn = findViewById(R.id.gallerybtn);
        _aboutBtn = findViewById(R.id.aboutbtn);
        // Set listener for all buttons
        _uploadBtn.setOnClickListener(this);
        _galleryBtn.setOnClickListener(this);
        _aboutBtn.setOnClickListener(this);
    }

    /**
     * An onClick listener for the buttons initialized on initializeButtons();
     *
     * @param v: MainActivity view
     */
    @Override
    public void onClick(View v) {

        // Determine clicked button
        switch (v.getId()) {
            case R.id.uploadbtn:
                // TODO: Verify that the server is up & running before allowing users to submit any images.
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

    /**
     * Prompts uploading options dialog
     */
    private void showUploadOptions() {
        // List the suggested options
        String[] options = {"Take a photo", "Import from device"};
        // Create a new AlertDialog instance
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set a title for the AlertDialog instance
        builder.setTitle("Upload image(s) from ...");
        // Set an icon for the AlertDialog instance
        builder.setIcon(R.drawable.uploadoptionicon);
        // Set suggested options to the AlertDialog && Handle selection.
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Option at index[0]
                if (which == 0) {
                    // If user chooses camera, then open camera.
                    if (ActivityCompat.checkSelfPermission(
                            MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                        // TODO: Handle the case if user refuse to grant camera permission
                        openDeviceCamera();
                    } else {
                        // Open camera.
                        openDeviceCamera();
                    }

                } else {
                    // Option other than option at index[0]
                    // If user chooses import from device, then open their storage.
                    openImageChooser();
                }
            }
        });
        builder.show();
    }

    /**
     * Opens internal gallery storage chooser.
     */
    private void openImageChooser() {
        // Create an Action_get_content intent
        Intent storageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // Set Action_get_content intent directory.
        storageIntent.setType("image/*");
        // Enable selecting multiple images form gallery
        storageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Create a chooser intent with a title.
        Intent chooser = Intent.createChooser(storageIntent, "Choose an image");
        // Start gallery chooser intent.
        startActivityForResult(chooser, FILE_SELECT_CODE);

        /*
         Note: The image chooser intent contents are handled on the onActivityResult(..);
         */
    }

    /**
     * Handling image chooser intent, and device camera intent.
     *
     * @param requestCode: Determines the last used intent.
     * @param resultCode:  Determines the status of the last used intent
     * @param data:        Executed intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check if the last used intent was the image chooser.
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE && data != null) {

            // Check if multiple images were selected
            if (data.getClipData() != null) {

                // Send selected images to the feedback view.
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    // Display image sequentially on the feedback view.
                    moveToFeedbackWindow(data.getClipData().getItemAt(i).getUri());
                    /*
                      Note: Maybe retreiving the items' Uri this way consumes more time!
                      Suggested sol.: Store all selected images' Uri in an array.
                     */
                }

            } else {
                // A single image was selected.
                Toast.makeText(this, "Single!", Toast.LENGTH_LONG).show();
                // Send the single image to the feedback view
                if (data.getData() != null) {
                    moveToFeedbackWindow(data.getData());
                }
            }
        }
        // If image is taken by the Camera
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            // send the captured image for feedback window.
            moveToFeedbackWindow(photoURI);
        }

    }

    /**
     * Switches current view to the feedback view.
     * Feedback view where results are displayed.
     *
     * @param selectedImageUri: The Uri of the image we want to work with.
     */
    private void moveToFeedbackWindow(Uri selectedImageUri) {
        // Create a new intent.
        Intent feedbackIntent = new Intent(MainActivity.this, Feedback_Window.class);
        // Pass image uri to the feedback view
        feedbackIntent.putExtra("ImageUri", selectedImageUri.toString());
        // Start the intent.
        startActivity(feedbackIntent);
    }

    /**
     * Opens the device's camera.
     */
    private void openDeviceCamera() {
        // Create new intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // more information will be added once i understand this! :)
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create file for image
            File photoFile = null;
            try {
                // Create an image file
                photoFile = createImageFile();
            } catch (IOException ex) {
                // error: file cannot be create
            }
            // Once the image file has been created.
            if (photoFile != null) {
                // Store captured image Uri.
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android fileprovider",
                        photoFile);
                // Pass the future taken image Uri to intent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Start the intent.
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Creates an image file for the camera.
     *
     * @return Future image Uri.
     * @throws IOException: Space, directory might not be found.
     */
    private File createImageFile() throws IOException {
        // Get current date and time.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Create image name
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Get picture directory path.
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Create the image file on the picture directory inside the device.
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Return image file instance.
        return image;
    }

    /**
     * Opens the application's gallery
     */
    private void launchGallery() {
        // Create a new intent for the gallery.
        Intent intent = new Intent(this, Gallery.class);
        // Switch to the gallery view.
        startActivity(intent);
    }
}
