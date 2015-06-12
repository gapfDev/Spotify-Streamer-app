package com.alxdev.spotifystreamerapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by GABRIEL on 11/06/2015.
 */
public class TopTenItem implements Parcelable {

    private String mTrackName;
    private String mAlbumImage;
    private String mAlbumName;

    public TopTenItem(Track track) {

        mAlbumName = track.album.name;
        mTrackName = track.name;
        mAlbumImage = Constants.IMAGE_DEFAULT;

        if (!track.album.images.isEmpty()) {
            mAlbumImage = track.album.images.get(0).url;
        }
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public String getmAlbumImage() {
        return mAlbumImage;
    }

    public String getmTrackName() {
        return mTrackName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTrackName);
        dest.writeString(this.mAlbumImage);
        dest.writeString(this.mAlbumName);
    }

    protected TopTenItem(Parcel in) {
        this.mTrackName = in.readString();
        this.mAlbumImage = in.readString();
        this.mAlbumName = in.readString();
    }

    public static final Parcelable.Creator<TopTenItem> CREATOR = new Parcelable.Creator<TopTenItem>() {
        public TopTenItem createFromParcel(Parcel source) {
            return new TopTenItem(source);
        }

        public TopTenItem[] newArray(int size) {
            return new TopTenItem[size];
        }
    };
}
