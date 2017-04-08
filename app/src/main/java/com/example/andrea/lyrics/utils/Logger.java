package com.example.andrea.lyrics.utils;

import android.util.Log;

/**
 * Created by andrea on 26/03/17.
 */

public class Logger {
    public static final String TAG = "lyrics_app";

    public static void debugMessage(String msg) {
        Log.d(TAG, msg);
    }

    public static void debugError(String msg) {
        Log.e(TAG, msg);
    }
}
