package ceg.seefood;

import android.os.Parcel;
import android.os.Parcelable;

public class GalleryItem implements Parcelable {

    String name, url;

    public GalleryItem(){

    }

    protected GalleryItem(Parcel parcel){
        name = parcel.readString();
        url = parcel.readString();
    }

    public static final  Creator<GalleryItem> CREATOR = new Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel source) {
            return new GalleryItem(source);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setUrl(String url){
        this.url = url;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeString(name);
        parcel.writeString(url);
    }

}
