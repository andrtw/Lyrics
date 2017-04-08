package com.example.andrea.lyrics;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by andrea on 01/04/17.
 */

public class SettingsManager {

    private static final String PREF_NAME = "com.example.andrea.lyrics.PREFERENCES";
    private static final String PREF_KEY_ENABLED = "enabled";

    public static void toggleEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(PREF_KEY_ENABLED, enabled);
        edit.apply();
    }

    public static boolean isEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_KEY_ENABLED, true);
    }
}
