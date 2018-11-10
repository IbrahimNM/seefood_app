package ceg.seefood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<GalleryItem> images  = new ArrayList<>();

    public GalleryAdapter(Context context, List<GalleryItem> images){
        this.context = context;
        this.images = images;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup group, int type){
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(group.getContext()).inflate(R.layout.gallery_item_layout,group,false);
        viewHolder = new ImageHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int index){

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);


        Glide.with(context).load(images.get(index).getUrl())
                .thumbnail(0.5f)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(options)
                .into(((ImageHolder)holder).img);
    }

    @Override
    public int getItemCount(){
        return images.size();
    }

    public static class ImageHolder extends RecyclerView.ViewHolder{

        ImageView img;

        public ImageHolder(View view){
            super(view);
            img = (ImageView)view.findViewById(R.id.img);
        }
    }
}
