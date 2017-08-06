package com.example.andrea.lyrics.model;

/**
 * Created by andrea on 01/08/17.
 */

public class Recent {

    private long id;
    private String artistName;
    private String songName;

    public Recent() {
    }

    public Recent(long id, String artistName, String songName) {
        this.id = id;
        this.artistName = artistName;
        this.songName = songName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", artist: " + artistName + ", song: " + songName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Recent) {
            Recent other = (Recent) obj;
            return (this.artistName.equals(other.artistName) && this.songName.equals(other.songName));
        }
        return false;
    }
}
