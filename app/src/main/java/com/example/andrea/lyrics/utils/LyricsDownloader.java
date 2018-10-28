package com.example.andrea.lyrics.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by andrea on 26/03/17.
 */

public class LyricsDownloader {

    public static final String NOT_FOUND_CODE = "not_found";
    private static final String NOT_FOUND = "<h1>Welcome to AZLyrics!</h1>";
    private static OkHttpClient mClient;

    static {
        mClient = new OkHttpClient();
    }

    public static void getSourceCode(String artist, String song, final DownloadListener listener) {
        Logger.debugMessage("LyricsDownloader.getSourceCode: " + artist + ", " + song);

        final String cleanedArtist = setupForUrl(artist, true);
        final String cleanedSong = setupForUrl(song, false);

        Request request = new Request.Builder()
                .url("http://www.azlyrics.com/lyrics/" + cleanedArtist + "/" + cleanedSong + ".html")
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.debugError("error downloading: " + e.getMessage());
                listener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                String result = html;

                if (html.contains(NOT_FOUND)) {
                    result = NOT_FOUND_CODE;
                }

                listener.onComplete(result);
            }
        });
    }

    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnectedOrConnecting();
            }
        }
        return false;
    }

    private static String setupForUrl(String str, boolean ignoreThe) {
        String s = str.trim().toLowerCase()
                .replaceAll("[^A-Za-z0-9]", "")
                .replace("&", "");
        if (ignoreThe) {
            return s.replace("the", "");
        }
        return s;
    }

    public interface DownloadListener {
        void onComplete(String html);

        void onError();
    }
}
