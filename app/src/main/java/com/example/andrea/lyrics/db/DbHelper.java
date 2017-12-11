package com.example.andrea.lyrics.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andrea on 31/03/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_ARTISTS =
            "CREATE TABLE " + DbContract.TABLE_ARTISTS +
                    " (" + DbContract.ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DbContract.ARTIST_NAME + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_SONGS =
            "CREATE TABLE " + DbContract.TABLE_SONGS +
                    " (" + DbContract.SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DbContract.SONG_NAME + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_RECENT =
            "CREATE TABLE " + DbContract.TABLE_RECENT +
                    " (" + DbContract.RECENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DbContract.RECENT_ARTIST_NAME + " TEXT NOT NULL, " +
                    DbContract.RECENT_SONG_NAME + " TEXT NOT NULL);";

    public DbHelper(Context context) {
        super(context, DbContract.DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_RECENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_RECENT);
        onCreate(db);
    }

    public void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_RECENT);
    }

    public void deleteRecentSearches(SQLiteDatabase db) {
        db.delete(DbContract.TABLE_RECENT, null, null);
    }

}
