package com.example.andrea.lyrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.andrea.lyrics.utils.Logger;

/**
 * Created by andrea on 31/03/17.
 */

public class SpotifyBroadcastReceiver extends BroadcastReceiver {

    static class BroadcastTypes {
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }
    private OnReceiveListener listener;
    private boolean doSearch;
    public static String artistName, trackName;

    public interface OnReceiveListener {
        void onReceive(String artist, String song);
    }

    public SpotifyBroadcastReceiver(OnReceiveListener listener) {
        this.listener = listener;
        doSearch = false;
        artistName = "";
        trackName = "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
            //String trackId = intent.getStringExtra("id");
            //String albumName = intent.getStringExtra("album");
            //int trackLengthInSec = intent.getIntExtra("length", 0);

            doSearch = true;

            artistName = intent.getStringExtra("artist");
            trackName = intent.getStringExtra("track");

            Logger.debugMessage("METADATA_CHANGED: " + artistName + ", " + trackName);
        }
        else if (action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            int positionInMs = intent.getIntExtra("playbackPosition", 0);

            Logger.debugMessage("PLAYBACK_STATE_CHANGED: " + playing + ", " + positionInMs);

            if (doSearch && playing && positionInMs <= 1000) {
                listener.onReceive(artistName, trackName);
            }
        }
        /*else if (action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
        }*/
    }
}
