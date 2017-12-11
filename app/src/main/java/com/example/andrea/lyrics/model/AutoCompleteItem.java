package com.example.andrea.lyrics.model;

/**
 * Created by andrea on 31/03/17.
 */

public class AutoCompleteItem {

    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_SONG = 1;

    private long mId;
    private int mType;
    private String mName;

    public AutoCompleteItem(long id, int type, String name) {
        this.mId = id;
        this.mType = type;
        this.mName = name;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public String print() {
        return "ID: " + mId + ", TYPE: " + parseType() + ", NAME: " + mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    private String parseType() {
        switch (mType) {
            case TYPE_ARTIST: return "artist";
            case TYPE_SONG: return "song";
            default: throw new RuntimeException("Unknown type");
        }
    }
}
