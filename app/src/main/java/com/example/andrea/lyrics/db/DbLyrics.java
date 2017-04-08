package com.example.andrea.lyrics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.andrea.lyrics.model.Item;
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

    public Item addArtist(String artistName) {
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
        Item artist = cursorToArtist(cursor);
        cursor.close();
        Logger.debugMessage("[DB] added artist. " + artist.print());
        return artist;
    }

    public Item addSong(String songName) {
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
        Item song = cursorToSong(cursor);
        cursor.close();
        Logger.debugMessage("[DB] added song. " + song.print());
        return song;
    }

    public void deleteArtist(Item artist) {
        long id = artist.getId();
        db.delete(DbContract.TABLE_ARTISTS, DbContract.ARTIST_ID + " = " + id, null);
        Logger.debugMessage("[DB] deleted artist. " + artist.print());
    }

    public void deleteSong(Item song) {
        long id = song.getId();
        db.delete(DbContract.TABLE_SONGS, DbContract.SONG_ID + " = " + id, null);
        Logger.debugMessage("[DB] deleted song. " + song.print());
    }

    public List<Item> getArtists() {
        List<Item> artists = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_ARTISTS, ALL_COLUMNS_ARTIST,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item a = cursorToArtist(cursor);
            artists.add(a);
            cursor.moveToNext();
        }
        cursor.close();
        return artists;
    }

    public List<Item> getSongs() {
        List<Item> songs = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_SONGS, ALL_COLUMNS_SONG,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item s = cursorToSong(cursor);
            songs.add(s);
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }




    private Item cursorToArtist(Cursor cursor) {
        return new Item(
                cursor.getLong(cursor.getColumnIndex(DbContract.ARTIST_ID)),
                Item.TYPE_ARTIST,
                cursor.getString(cursor.getColumnIndex(DbContract.ARTIST_NAME)));
    }

    private Item cursorToSong(Cursor cursor) {
        return new Item(
                cursor.getLong(cursor.getColumnIndex(DbContract.SONG_ID)),
                Item.TYPE_SONG,
                cursor.getString(cursor.getColumnIndex(DbContract.SONG_NAME)));
    }

    private List<String> getArtistsNames() {
        List<String> artists = new ArrayList<>();

        Cursor cursor = db.query(DbContract.TABLE_ARTISTS, ALL_COLUMNS_ARTIST,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item a = cursorToArtist(cursor);
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
            Item s = cursorToSong(cursor);
            songs.add(s.getName());
            cursor.moveToNext();
        }
        cursor.close();
        return songs;
    }

}
