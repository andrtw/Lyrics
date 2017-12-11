package com.example.andrea.lyrics.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrea on 26/03/17.
 */

public class Lyrics implements Parcelable {
    public static final Parcelable.Creator<Lyrics> CREATOR
            = new Parcelable.Creator<Lyrics>() {
        public Lyrics createFromParcel(Parcel in) {
            return new Lyrics(in);
        }

        public Lyrics[] newArray(int size) {
            return new Lyrics[size];
        }
    };
    private String mArtistName;
    private String mSongName;
    private String mLyrics;

    public Lyrics() {
    }

    public Lyrics(String artistName, String songName, String lyrics) {
        this.mArtistName = artistName;
        this.mSongName = songName;
        this.mLyrics = lyrics;
    }

    private Lyrics(Parcel in) {
        this.mArtistName = in.readString();
        this.mSongName = in.readString();
        this.mLyrics = in.readString();
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        this.mArtistName = artistName;
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;
    }

    public String getLyrics() {
        return mLyrics;
    }

    public void setLyrics(String lyrics) {
        this.mLyrics = lyrics;
    }

    public void setLyrics(Context context, int id) {
        this.mLyrics = context.getResources().getString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mArtistName);
        dest.writeString(mSongName);
        dest.writeString(mLyrics);
    }
}
