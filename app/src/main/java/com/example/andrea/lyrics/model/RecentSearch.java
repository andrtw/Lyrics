package com.example.andrea.lyrics.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrea on 01/08/17.
 */

public class RecentSearch implements Parcelable {

    public static final Parcelable.Creator<RecentSearch> CREATOR
            = new Parcelable.Creator<RecentSearch>() {
        public RecentSearch createFromParcel(Parcel in) {
            return new RecentSearch(in);
        }

        public RecentSearch[] newArray(int size) {
            return new RecentSearch[size];
        }
    };
    private long mId;
    private String mArtistName;
    private String mSongName;

    public RecentSearch(long id, String artistName, String songName) {
        this.mId = id;
        this.mArtistName = artistName;
        this.mSongName = songName;
    }

    private RecentSearch(Parcel in) {
        this.mId = in.readInt();
        this.mArtistName = in.readString();
        this.mSongName = in.readString();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
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

    @Override
    public String toString() {
        return "ID: " + mId + ", artist: " + mArtistName + ", song: " + mSongName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecentSearch) {
            RecentSearch other = (RecentSearch) obj;
            return (this.mArtistName.equals(other.mArtistName) && this.mSongName.equals(other.mSongName));
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mArtistName);
        dest.writeString(mSongName);
    }
}
