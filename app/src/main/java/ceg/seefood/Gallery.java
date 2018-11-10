package ceg.seefood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class Gallery extends AppCompatActivity {

    GalleryAdapter adapter;
    RecyclerView recyclerView;

    ArrayList<GalleryItem> images = new ArrayList<>();

    public String IMGS[] = {
            "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2018/6/0/FN_snapchat_coachella_wingman%20.jpeg.rend.hgtvcom.616.462.suffix/1523633513292.jpeg",
            "https://images.unsplash.com/photo-1532022900249-74610fde3038?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=0338782777520b5dfcf1b6dc5daf35e8&auto=format&fit=crop&w=1000&q=60",
            "https://images.unsplash.com/photo-1511516412963-801b050c92aa?ixlib=rb-0.3.5&s=e12fd2d93d316b892db9ec20d5c904d3&auto=format&fit=crop&w=1050&q=80",
            "https://images.unsplash.com/photo-1531947398206-60f8e97f34a2?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=981b88e56d0d329347e68f1831a57b08&auto=format&fit=crop&w=1000&q=60"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        for(int i = 0; i < IMGS.length; i++){
            GalleryItem item = new GalleryItem();
            item.setName("Image " + i);
            item.setUrl(IMGS[i]);
            images.add(item);
        }

        recyclerView = (RecyclerView)findViewById(R.id.gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setHasFixedSize(true);

        adapter = new GalleryAdapter(Gallery.this, images);
        recyclerView.setAdapter(adapter);
    }
}
