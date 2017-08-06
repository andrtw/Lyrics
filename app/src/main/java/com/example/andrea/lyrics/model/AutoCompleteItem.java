package com.example.andrea.lyrics.model;

/**
 * Created by andrea on 31/03/17.
 */

public class AutoCompleteItem {

    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_SONG = 1;

    private long id;
    private int type;
    private String name;

    public AutoCompleteItem(long id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String print() {
        return "ID: " + id + ", TYPE: " + parseType() + ", NAME: " + name;
    }

    @Override
    public String toString() {
        return name;
    }

    private String parseType() {
        switch (type) {
            case TYPE_ARTIST: return "artist";
            case TYPE_SONG: return "song";
            default: throw new RuntimeException("Unknown type");
        }
    }
}
