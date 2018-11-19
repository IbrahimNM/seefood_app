package ceg.seefood;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Feedback_Window extends AppCompatActivity {

    ImageView picture;
    TextView _theResult, _theStats;
    final String serverURL  = "http://seefood.moostermiko.com/appsubmit";
    Bitmap mImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback__window);

        // init image view
        picture = (ImageView) findViewById(R.id.theImage);
        _theResult = (TextView) findViewById(R.id.theResult);
        _theStats = (TextView) findViewById(R.id.theStatistics);

        // Retrieve passed data
        String passedUri = getIntent().getStringExtra("ImageUri");
        // Parse the String to an actual Uri
        Uri imageUri = Uri.parse(passedUri);

        // Create a file name for submittion
        final String fileName = new SimpleDateFormat("yyyyMMddHHmmss'.jpeg'").format(new Date());

        // Display image to user
        picture.setImageURI(imageUri);
        try {
            // Convert image to bitmap.
            mImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // -------------- Sends image to seefood server -------------------------
            Toast.makeText(getApplicationContext(), "Analyzing image ....", Toast.LENGTH_LONG).show();
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            // Initialize a new ImageRequest
            StringRequest imageRequest = new StringRequest(Request.Method.POST,
                    serverURL, // Image URL
                    new Response.Listener<String>() { // Bitmap listener
                        @Override
                        public void onResponse(String response) {
                            // Display reponse to user
                            _theResult.setText(response.substring(0, response.indexOf('[')).toUpperCase());
                            _theStats.setText(response.substring(response.indexOf('[')+2, response.length()-3));

                        }
                    },
                    new Response.ErrorListener() { // Error listener
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Failed to connect to server! Please try again ...", Toast.LENGTH_LONG).show();

                        }
                    }){

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("file", encodeToBase64(mImage, Bitmap.CompressFormat.JPEG, 100));
                    params.put("name", fileName );
                    return params;
                }
            };
            imageRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            // Add ImageRequest to the RequestQueu
            requestQueue.add(imageRequest);

        } catch (IOException e){
            // if image is not found.
            Toast.makeText(getApplicationContext(), "Error: Image not found!", Toast.LENGTH_LONG).show();
        }

    }

    // Convert Image to Base64 String
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        Log.i("TAG", Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT));
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
