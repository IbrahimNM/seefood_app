package ceg.seefood;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Feedback_Window extends AppCompatActivity {

    ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback__window);

        // init image view
        picture = (ImageView) findViewById(R.id.theImage);
        // Retrieve passed data
        String passedUri = getIntent().getStringExtra("ImageUri");
        // Parse the String to an actual Uri
        Uri imageUri = Uri.parse(passedUri);

        // TODO: Convert image to bitmap.
        // TODO: Convert bitmap to base46 string.
        // TODO: Send image to seefood server.
        // Display image to user
        picture.setImageURI(imageUri);
        // TODO: Display reponse to user
    }
}
