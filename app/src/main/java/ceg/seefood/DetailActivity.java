package ceg.seefood;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    public ArrayList<GalleryItem> images = new ArrayList<>();
    int pos;
    Toolbar toolbar;
    private ViewPager mViewPager;

    /*
    * Creates a viewPager that will show a slightly larger version of each image, and will allow
    * the user to swipe through the images contained in the gallery.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        images = getIntent().getParcelableArrayListExtra("images");
        pos = getIntent().getIntExtra("pos", 0);

        setTitle(images.get(pos).getName());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), images);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setTitle(images.get(i).getName());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        String name;
        String url;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";

        @Override
        public void setArguments(Bundle bundle){
            super.setArguments(bundle);
            this.pos = bundle.getInt(ARG_SECTION_NUMBER);
            this.name = bundle.getString(ARG_IMG_TITLE);
            this.url = bundle.getString(ARG_IMG_URL);
        }

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String name, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, name);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onStart(){
            super.onStart();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);
            Glide.with(getActivity()).load(url).thumbnail(0.1f).into(imageView);

            /*
            * If the image currently in view of the viewPager is selected again by the user,
            * it will be resubmitted to the server for the results on its analysis
            */
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent feedbackIntent = new Intent(getActivity(), Feedback_Window.class);
                    try {
                        File tmp = new File(url);
                        feedbackIntent.putExtra("ImageUri", Uri.fromFile(tmp).toString());
                        startActivity(feedbackIntent);
                    } catch (Exception s){

                    }

                }
            });
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<GalleryItem> images = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<GalleryItem> images) {
            super(fm);
            this.images = images;
        }

        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position, images.get(position).getName(), images.get(position).getUrl());
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return images.get(position).getName();
        }
    }
}
