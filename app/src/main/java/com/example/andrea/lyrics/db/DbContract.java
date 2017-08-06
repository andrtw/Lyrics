package com.example.andrea.lyrics.db;

/**
 * Created by andrea on 31/03/17.
 */

public class DbContract {
    public static final String DB_NAME = "lyrics.db";

    public static final String TABLE_ARTISTS = "artists";
    public static final String ARTIST_ID = "_id";
    public static final String ARTIST_NAME = "artist_name";

    public static final String TABLE_SONGS = "songs";
    public static final String SONG_ID = "_id";
    public static final String SONG_NAME = "song_name";

    public static final String TABLE_RECENT = "recent";
    public static final String RECENT_ID = "_id";
    public static final String RECENT_ARTIST_NAME = "recent_artist_name";
    public static final String RECENT_SONG_NAME = "recent_song_name";
    public static final int RECENT_MAX = 4;
}
