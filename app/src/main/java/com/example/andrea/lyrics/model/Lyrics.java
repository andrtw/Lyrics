package com.example.andrea.lyrics.model;

import android.content.Context;

/**
 * Created by andrea on 26/03/17.
 */

public class Lyrics {
    private String artistName;
    private String songName;
    private String lyrics;

    public Lyrics() {
    }

    public Lyrics(String artistName, String songName, String lyrics) {
        this.artistName = artistName;
        this.songName = songName;
        this.lyrics = lyrics;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongName() {
        return songName;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void setLyrics(Context context, int id) {
        this.lyrics = context.getResources().getString(id);
    }
}
