package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {
    @SerializedName("url")
    private String url;
    @SerializedName("alt")
    private String alt;

    public Image(String url, String alt) {
        this.url = url;
        this.alt = alt;
    }

    protected Image(Parcel in) {
        url = in.readString();
        alt = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(alt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUrl() { return url; }
    public String getAlt() { return alt; }

    public void setUrl(String url) { this.url = url; }
    public void setAlt(String alt) { this.alt = alt; }
}