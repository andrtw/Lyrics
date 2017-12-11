package com.example.andrea.lyrics;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import com.example.andrea.lyrics.db.DbLyrics;
import com.example.andrea.lyrics.fragments.LyricsFragment;
import com.example.andrea.lyrics.fragments.RecentSearchesFragment;
import com.example.andrea.lyrics.model.Lyrics;
import com.example.andrea.lyrics.utils.HtmlParser;
import com.example.andrea.lyrics.utils.Logger;
import com.example.andrea.lyrics.utils.LyricsDownloader;
import com.example.andrea.lyrics.views.SearchDialog;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements
        RecentSearchesFragment.OnRecentSearchesFragmentListener,
        LyricsFragment.OnLyricsFragmentListener,
        SearchDialog.SearchDialogListener {

    // UI
    private ProgressBar progress;

    // search dialog
    private SearchDialog mSearchDialog;

    private SpotifyBroadcastReceiver broadcastReceiver;
    private DbLyrics db;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbLyrics(this);
        db.open();
        handler = new Handler();

        progress = (ProgressBar) findViewById(R.id.progress);

        mSearchDialog = new SearchDialog();

        // load the default fragment (recent searches)
        RecentSearchesFragment recentSearchesFragment =
                RecentSearchesFragment.newInstance(db.getRecentSearches());
        changeFragment(recentSearchesFragment, "recent_searches_fragment");

        broadcastReceiver = new SpotifyBroadcastReceiver(this, new SpotifyBroadcastReceiver.OnReceiveListener() {
            @Override
            public void onReceive(String artist, String song) {
                search(artist, song);
            }
        });

        // restore search dialog artist and song
        if (savedInstanceState != null) {
            String lastArtist = "", lastSong = "";
            if (savedInstanceState.containsKey("search_artist")) {
                lastArtist = savedInstanceState.getString("search_artist");
                mSearchDialog.setLastArtist(lastArtist);
            }
            if (savedInstanceState.containsKey("search_song")) {
                lastSong = savedInstanceState.getString("search_song");
                mSearchDialog.setLastSong(lastSong);
            }
            // do search if needed
            if (!lastArtist.isEmpty() && !lastSong.isEmpty()) {
                search(lastArtist, lastSong);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isSearchDialogVisible()) {
            outState.putString("search_artist", mSearchDialog.getArtist());
            outState.putString("search_song", mSearchDialog.getSong());
        } else {
            outState.putString("search_artist", mSearchDialog.getLastArtist());
            outState.putString("search_song", mSearchDialog.getLastSong());
        }
    }

    public DbLyrics getDb() {
        return db;
    }

    private void search(final String artist, final String song) {
        Logger.debugMessage("Search: " + artist + ", " + song);

        setProgressVisible(true);
        hideSearchDialog();

        LyricsDownloader.getSourceCode(artist, song, new LyricsDownloader.DownloadListener() {
            @Override
            public void onComplete(String html) {
                setProgressVisible(false);

                final HashMap<String, Object> parsed = handleLyricsErrors(HtmlParser.parseSourceCode(html));
                final Lyrics lyrics = (Lyrics) parsed.get("lyrics");
                final boolean errors = (Boolean) parsed.get("errors");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LyricsFragment lyricsFragment = LyricsFragment.newInstance(lyrics);
                        changeFragment(lyricsFragment, "lyrics_fragment");

                        // save artist and song for autocomplete suggestions
                        if (!errors) {
                            saveArtistAndSong(lyrics.getArtistName(), lyrics.getSongName());
                        }

                        mSearchDialog.setLastArtist(lyrics.getArtistName());
                        mSearchDialog.setLastSong(lyrics.getSongName());

                        broadcastReceiver.setArtistName(lyrics.getArtistName());
                        broadcastReceiver.setTrackName(lyrics.getSongName());
                    }
                });
            }

            @Override
            public void onError() {
                setProgressVisible(false);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean online = LyricsDownloader.isOnline(MainActivity.this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder
                                .setTitle(R.string.error_dialog_title)
                                .setMessage(online ? R.string.error_dialog_unknown : R.string.error_dialog_offline)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });
    }

    private void saveArtistAndSong(String artist, String song) {
        db.addArtist(artist);
        db.addSong(song);
        db.addRecentSearch(artist, song);
    }

    private HashMap<String, Object> handleLyricsErrors(Lyrics lyrics) {
        HashMap<String, Object> map = new HashMap<>();
        boolean errors = false;

        if (lyrics.getLyrics().equals(LyricsDownloader.NOT_FOUND_CODE)) {
            errors = true;
            lyrics.setLyrics(MainActivity.this, R.string.error_lyrics_not_found);
        }

        map.put("errors", errors);
        map.put("lyrics", lyrics);
        return map;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                // go to home (recent searches)
                RecentSearchesFragment recentSearchesFragment = RecentSearchesFragment.newInstance(db.getRecentSearches());
                changeFragment(recentSearchesFragment, "recent_searches_fragment");
                return true;
            case R.id.menu_search_current:
                // search current playing song
                search(broadcastReceiver.getArtistName(), broadcastReceiver.getTrackName());
                return true;
            case R.id.menu_search:
                // show search dialog
                showSearchDialog();
                return true;
            case R.id.menu_settings:
                // go to settings
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        db.open();
        registerReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        db.close();
        unregisterReceiver();
        super.onPause();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpotifyBroadcastReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED);
        filter.addAction(SpotifyBroadcastReceiver.BroadcastTypes.QUEUE_CHANGED);
        filter.addAction(SpotifyBroadcastReceiver.BroadcastTypes.METADATA_CHANGED);
        this.registerReceiver(broadcastReceiver, filter);
        Logger.debugMessage("Broadcast registered");
    }

    private void unregisterReceiver() {
        if (SettingsManager.isAutoSearchEnabled(MainActivity.this)) {
            this.unregisterReceiver(broadcastReceiver);
            Logger.debugMessage("Broadcast unregistered");
        }
    }

    private void setProgressVisible(final boolean visible) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (visible) progress.setVisibility(View.VISIBLE);
                else progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void changeFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    private void showSearchDialog() {
        mSearchDialog.show(getSupportFragmentManager(), "search_dialog");
    }

    private void hideSearchDialog() {
        // the search dialog might have not been added yet and it would take to a NPE
        if (isSearchDialogVisible()) {
            mSearchDialog.dismiss();
            mSearchDialog.hideKeyboard();
        }
    }

    private boolean isSearchDialogVisible() {
        return mSearchDialog.isAdded();
    }

    @Override
    public void onRecentSearchClick(String artistName, String songName) {
        search(artistName, songName);
    }

    @Override
    public void onSearch(String artistName, String songName) {
        search(artistName.trim(), songName.trim());
    }

    @Override
    public void onSearchWithIME(int actionId, String artistName, String songName) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search(artistName, songName);
        }
    }
}
