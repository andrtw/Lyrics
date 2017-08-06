package com.example.andrea.lyrics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andrea.lyrics.db.DbLyrics;
import com.example.andrea.lyrics.model.AutoCompleteItem;
import com.example.andrea.lyrics.model.Lyrics;
import com.example.andrea.lyrics.model.Recent;
import com.example.andrea.lyrics.utils.Animations;
import com.example.andrea.lyrics.utils.HtmlParser;
import com.example.andrea.lyrics.utils.Logger;
import com.example.andrea.lyrics.utils.LyricsDownloader;
import com.example.andrea.lyrics.views.InterceptableScrollView;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // UI
    private RelativeLayout overlay;
    private InterceptableScrollView scrollView;
    private TextView lyricsText;
    private TextView artistSongText;
    private RelativeLayout searchLayout;
    private AutoCompleteTextView searchArtist, searchSong;

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

        overlay = (RelativeLayout) findViewById(R.id.overlay);
        scrollView = (InterceptableScrollView) findViewById(R.id.scroll_view);
        lyricsText = (TextView) findViewById(R.id.lyrics_text);
        artistSongText = (TextView) findViewById(R.id.artist_song_text);
        searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
        searchArtist = (AutoCompleteTextView) findViewById(R.id.search_artist);
        searchSong = (AutoCompleteTextView) findViewById(R.id.search_song);
        ImageButton searchBtn = (ImageButton) findViewById(R.id.search_btn);
        TextView clearArtist = (TextView) findViewById(R.id.clear_artist);
        TextView clearSong = (TextView) findViewById(R.id.clear_song);

        searchBtn.setOnClickListener(this);
        clearArtist.setOnClickListener(this);
        clearSong.setOnClickListener(this);

        setupArtistAutocomplete();
        setupSongAutocomplete();

        searchSong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch(searchArtist.getText().toString().trim(), searchSong.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        broadcastReceiver = new SpotifyBroadcastReceiver(new SpotifyBroadcastReceiver.OnReceiveListener() {
            @Override
            public void onReceive(String artist, String song) {
                if (searchLayout.getVisibility() != View.VISIBLE) {
                    search(artist, song);
                }
            }
        });

        restoreState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("search_open", searchLayout.getVisibility() == View.VISIBLE);
        outState.putString("search_artist", searchArtist.getText().toString());
        outState.putString("search_song", searchSong.getText().toString());
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // search layout
            Boolean searchOpen = getSavedInstanceValue(savedInstanceState, "search_open");
            if (searchOpen != null) {
                if (searchOpen) openSearch();
                else closeSearch();
            }

            // artist name
            String artist = getSavedInstanceValue(savedInstanceState, "search_artist");
            if (artist != null) {
                searchArtist.setText(artist);
            }

            // song name
            String song = getSavedInstanceValue(savedInstanceState, "search_song");
            if (song != null) {
                searchSong.setText(song);
            }

            // do search if needed
            if (artist != null && song != null) {
                doSearch(searchArtist.getText().toString().trim(), searchSong.getText().toString().trim());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn: {
                doSearch(searchArtist.getText().toString().trim(), searchSong.getText().toString().trim());
            }
            break;
            case R.id.clear_artist: {
                searchArtist.setText("");
                searchArtist.requestFocus();
            }
            break;
            case R.id.clear_song: {
                searchSong.setText("");
                searchSong.requestFocus();
            }
            break;
        }
    }

    private void search(final String artist, final String song) {
        LyricsDownloader.getSourceCode(artist, song, new LyricsDownloader.DownloadListener() {
            @Override
            public void onComplete(String html) {
                final HashMap<String, Object> parsed = handleLyricsErrors(HtmlParser.parseSourceCode(html));
                final Lyrics lyrics = (Lyrics) parsed.get("lyrics");
                final boolean errors = (Boolean) parsed.get("errors");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        lyricsText.setText(Html.fromHtml(lyrics.getLyrics()));
                        artistSongText.setText(lyrics.getArtistName() + " | " + lyrics.getSongName());
                        closeSearch();

                        if (!errors) {
                            saveArtistAndSong(lyrics.getArtistName(), lyrics.getSongName());
                        }

                        searchArtist.setText(lyrics.getArtistName());
                        setupArtistAutocomplete();

                        searchSong.setText(lyrics.getSongName());
                        setupSongAutocomplete();

                        SpotifyBroadcastReceiver.artistName = lyrics.getArtistName();
                        SpotifyBroadcastReceiver.trackName = lyrics.getSongName();
                    }
                });
            }

            @Override
            public void onError() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean online = LyricsDownloader.isOnline(MainActivity.this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder
                                .setTitle(R.string.error_dialog_title)
                                .setMessage(online ? R.string.error_dialog_unknown : R.string.error_dialog_offline)
                                .setPositiveButton(R.string.ok_up, new DialogInterface.OnClickListener() {
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

    private void doSearch(String artist, String song) {
        if (artist.isEmpty() && song.isEmpty()) {
            closeSearch();
            searchArtist.setText("");
            searchSong.setText("");
        }
        else {
            search(artist, song);
        }
        scrollView.smoothScrollTo(0, 0);
    }

    private void saveArtistAndSong(String artist, String song) {
        db.addArtist(artist);
        db.addSong(song);
        db.addRecent(artist, song);
    }

    private void openSearch() {
        Animations.open(this, searchLayout);
        Animations.open(this, overlay);
        scrollView.setScrollable(false);
    }

    private void closeSearch() {
        Animations.close(this, searchLayout);
        Animations.close(this, overlay);
        scrollView.setScrollable(true);
        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchArtist.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(searchSong.getWindowToken(), 0);
    }

    private void setupArtistAutocomplete() {
        AutoCompleteAdapter artistAutocomplete = new AutoCompleteAdapter(MainActivity.this, db.getArtists(), db, new AutoCompleteAdapter.AutocompleteListener() {
            @Override
            public void onDelete() {
                setupArtistAutocomplete();
            }
        });
        searchArtist.setAdapter(artistAutocomplete);
    }

    private void setupSongAutocomplete() {
        AutoCompleteAdapter songAutocomplete = new AutoCompleteAdapter(MainActivity.this, db.getSongs(), db, new AutoCompleteAdapter.AutocompleteListener() {
            @Override
            public void onDelete() {
                setupSongAutocomplete();
            }
        });
        searchSong.setAdapter(songAutocomplete);
    }

    private HashMap<String, Object> handleLyricsErrors(Lyrics lyrics) {
        HashMap<String, Object> map  = new HashMap<>();
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
            case R.id.menu_search_current:
                doSearch(SpotifyBroadcastReceiver.artistName, SpotifyBroadcastReceiver.trackName);
                return true;
            case R.id.menu_search:
                int visibility = searchLayout.getVisibility();
                if (visibility != View.VISIBLE) {
                    openSearch();
                }
                else {
                    closeSearch();
                }
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
            closeSearch();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        db.open();

        if (SettingsManager.isEnabled(MainActivity.this)) {
            registerReceiver();
        }

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
        if (SettingsManager.isEnabled(MainActivity.this)) {
            this.unregisterReceiver(broadcastReceiver);
            Logger.debugMessage("Broadcast unregistered");
        }
    }

    private <T> T getSavedInstanceValue(Bundle savedInstanceBundle, String key) {
        if (savedInstanceBundle.containsKey(key)) {
            return (T) savedInstanceBundle.get(key);
        }
        return null;
    }
}
