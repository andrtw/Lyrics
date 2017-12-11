package com.example.andrea.lyrics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.andrea.lyrics.model.AutoCompleteItem;
import com.example.andrea.lyrics.model.RecentSearch;
import com.example.andrea.lyrics.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrea on 31/03/17.
 */

public class DbLyrics {

    private SQLiteDatabase db;
    private DbHelper dbHelper;
    private static final String[] ALL_COLUMNS_ARTIST = {
            DbContract.ARTIST_ID,
            DbContract.ARTIST_NAME
    };
    private static final String[] ALL_COLUMNS_SONG = {
            DbContract.SONG_ID,
            DbContract.SONG_NAME
    };
    private static final String[] ALL_COLUMNS_RECENT = {
            DbContract.RECENT_ID,
            DbContract.RECENT_ARTIST_NAME,
            DbContract.RECENT_SONG_NAME
    };


    public DbLyrics(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public void drop() {
        dbHelper.drop(db);
    }

    public void deleteRecentSearches() {
        dbHelper.deleteRecentSearches(db);
    }

    public AutoCompleteItem addArtist(String artistName) {
        List<String> artistsNames = getArtistsNames();
        if (artistsNames.contains(artistName)) {
            Logger.debugMessage("[DB] artist already exists. " + artistName);
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(DbContract.ARTIST_NAME, artistName);
        long insertId = db.insert(DbContract.TABLE_ARTISTS, null, values);
        Cursor cursor = db.query(DbContract.TABLE_ARTISTS, ALL_COLUMNS_ARTIST,
                DbContract.ARTIST_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        AutoCompleteItem artist = cursorToArtist(cursor);
        cursor.close();
        Logger.debugMessage("[DB] added artist. " + artist.print());
        return artist;
    }

    public RecentSearch addRecentSearch(String artistName, String songName) {
        List<RecentSearch> recentSearches = getRecentSearches();
        // check if the same recent already exists
        if (recentSearches.contains(new RecentSearch(-1, artistName, songName))) {
            Logger.debugMessage("[DB] recent search already exists. " + artistName + ", " + songName);
            return null;
        }
        // check if max num of recent searches is reached
        if (recentSearches.size() >= DbContract.RECENT_MAX) {
            deleteFirstRecentSearch();
        }
        ContentValues values = new ContentValues();
        values.put(DbContract.RECENT_ARTIST_NAME, artistName);
        values.put(DbContract.RECENT_SONG_NAME, songName);
        long insertId = db.insert(DbContract.TABLE_RECENT, null, values);
        Cursor cursor = db.query(DbContract.TABLE_RECENT, ALL_COLUMNS_RECENT,
                DbContract.RECENT_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        RecentSearch recentSearch = cursorToRecentSearch(cursor);
        cursor.close();
        Logger.debugMessage("[DB] added recent search. " + recentSearch.toString());
        return recentSearch;
    }

    public AutoCompleteItem addSong(String songName) {
        List<String> songsNames = getSongsNames();
        if (songsNames.contains(songName)) {
            Logger.debugMessage("[DB] song already exists. " + songName);
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(DbContract.SONG_NAME, songName);
        long insertId = db.insert(DbContract.TABLE_SONGS, null, values);
        Cursor cursor = db.query(DbContract.TABLE_SONGS, ALL_COLUMNS_SONG,
                DbContract.SONG_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        AutoCompleteItem song = cursorToSong(cursor);
        cursor.close();
        Logger.debugMessage("[DB] added song. " + song.print());
        return song;
    }

    public void deleteArtist(AutoCompleteItem artist) {
        long id = artist.getId();
        db.delete(DbContract.TABLE_ARTISTS, DbContract.ARTIST_ID + " = " + id, null);
        Logger.debugMessage("[DB] deleted artist. " + artist.print());
    }

    public void deleteSong(AutoCompleteItem song) {
        long id = song.getId();
        db.delete(DbContract.TABLE_SONGS, DbContract.SONG_ID + " = " + id, null);
        Logger.debugMessage("[DB] deleted song. " + song.print());
    }

    private void deleteFirstRecentSearch() {
        Cursor cursor = db.query(DbContract.TABLE_RECENT, ALL_COLUMNS_RECENT,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(DbContract.RECENT_ID));
            db.delete(DbContract.TABLE_RECENT, DbContract.RECENT_ID + "=?", new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    public List<AutoCompleteItem> getArtists() {
        List<AutoCompleteItem> artists = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_ARTISTS, ALL_COLUMNS_ARTIST,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AutoCompleteItem a = cursorToArtist(cursor);
            artists.add(a);
            cursor.moveToNext();
        }
        cursor.close();
        return artists;
    }

    public List<AutoCompleteItem> getSongs() {
        List<AutoCompleteItem> songs = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_SONGS, ALL_COLUMNS_SONG,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AutoCompleteItem s = cursorToSong(cursor);
            songs.add(s);
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

    public List<RecentSearch> getRecentSearches() {
        List<RecentSearch> recentSearches = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_RECENT, ALL_COLUMNS_RECENT,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RecentSearch r = cursorToRecentSearch(cursor);
            recentSearches.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return recentSearches;
    }


    private AutoCompleteItem cursorToArtist(Cursor cursor) {
        return new AutoCompleteItem(
                cursor.getLong(cursor.getColumnIndex(DbContract.ARTIST_ID)),
                AutoCompleteItem.TYPE_ARTIST,
                cursor.getString(cursor.getColumnIndex(DbContract.ARTIST_NAME)));
    }

    private AutoCompleteItem cursorToSong(Cursor cursor) {
        return new AutoCompleteItem(
                cursor.getLong(cursor.getColumnIndex(DbContract.SONG_ID)),
                AutoCompleteItem.TYPE_SONG,
                cursor.getString(cursor.getColumnIndex(DbContract.SONG_NAME)));
    }

    private RecentSearch cursorToRecentSearch(Cursor cursor) {
        return new RecentSearch(
                cursor.getLong(cursor.getColumnIndex(DbContract.RECENT_ID)),
                cursor.getString(cursor.getColumnIndex(DbContract.RECENT_ARTIST_NAME)),
                cursor.getString(cursor.getColumnIndex(DbContract.RECENT_SONG_NAME)));
    }

    private List<String> getArtistsNames() {
        List<String> artists = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_ARTISTS, ALL_COLUMNS_ARTIST,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AutoCompleteItem a = cursorToArtist(cursor);
            artists.add(a.getName());
            cursor.moveToNext();
        }
        cursor.close();
        return artists;
    }

    private List<String> getSongsNames() {
        List<String> songs = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_SONGS, ALL_COLUMNS_SONG,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AutoCompleteItem s = cursorToSong(cursor);
            songs.add(s.getName());
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

}
