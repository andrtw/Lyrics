package com.example.andrea.lyrics.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.andrea.lyrics.AutoCompleteAdapter;
import com.example.andrea.lyrics.MainActivity;
import com.example.andrea.lyrics.R;
import com.example.andrea.lyrics.db.DbLyrics;

/**
 * Created by andrea on 11/12/2017.
 */

public class SearchDialog extends DialogFragment {

    private SearchDialogListener mListener;

    private DbLyrics mDb;

    private AutoCompleteTextView mSearchArtist, mSearchSong;
    private String mLastArtist = "", mLastSong = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDb = ((MainActivity) getActivity()).getDb();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View searchView = inflater.inflate(R.layout.dialog_search, null);

        // text input
        mSearchArtist = (AutoCompleteTextView) searchView.findViewById(R.id.search_artist);
        mSearchSong = (AutoCompleteTextView) searchView.findViewById(R.id.search_song);
        mSearchArtist.setText(mLastArtist);
        mSearchSong.setText(mLastSong);
        setupArtistAutocomplete();
        setupSongAutocomplete();

        // search button and from last input
        ImageButton searchBtn = (ImageButton) searchView.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSearch(mSearchArtist.getText().toString(), mSearchSong.getText().toString());
            }
        });
        mSearchSong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mListener.onSearchWithIME(actionId, mSearchArtist.getText().toString(), mSearchSong.getText().toString());
                return true;
            }
        });

        // clear input
        TextView clearArtist = (TextView) searchView.findViewById(R.id.clear_artist);
        TextView clearSong = (TextView) searchView.findViewById(R.id.clear_song);
        ClearSearchInput clearSearchInput = new ClearSearchInput(getActivity());
        clearArtist.setOnClickListener(clearSearchInput);
        clearSong.setOnClickListener(clearSearchInput);

        builder.setView(searchView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    public String getLastArtist() {
        return mLastArtist;
    }

    public void setLastArtist(String lastArtist) {
        mLastArtist = lastArtist;
    }

    public String getLastSong() {
        return mLastSong;
    }

    public void setLastSong(String lastSong) {
        mLastSong = lastSong;
    }

    public String getArtist() {
        if (mSearchArtist != null) {
            return mSearchArtist.getText().toString();
        }
        return "";
    }

    public String getSong() {
        if (mSearchSong != null) {
            return mSearchSong.getText().toString();
        }
        return "";
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SearchDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SearchDialogListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mLastArtist = mSearchArtist.getText().toString();
        mLastSong = mSearchSong.getText().toString();
        super.onDismiss(dialog);
    }

    private void setupArtistAutocomplete() {
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(), mDb,
                AutoCompleteAdapter.AUTOCOMPLETE_ARTIST, new AutoCompleteAdapter.AutocompleteListener() {
            @Override
            public void onDelete() {
                setupArtistAutocomplete();
            }
        });
        mSearchArtist.setAdapter(adapter);
    }

    private void setupSongAutocomplete() {
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(), mDb,
                AutoCompleteAdapter.AUTOCOMPLETE_SONG, new AutoCompleteAdapter.AutocompleteListener() {
            @Override
            public void onDelete() {
                setupSongAutocomplete();
            }
        });
        mSearchSong.setAdapter(adapter);
    }

    public interface SearchDialogListener {
        void onSearch(String artistName, String songName);

        void onSearchWithIME(int actionId, String artistName, String songName);
    }

    private class ClearSearchInput implements View.OnClickListener {

        private InputMethodManager mImm;

        private ClearSearchInput(Context context) {
            mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clear_artist:
                    mSearchArtist.setText("");
                    mSearchArtist.requestFocus();
                    mImm.showSoftInput(mSearchArtist, InputMethodManager.RESULT_SHOWN);
                    break;
                case R.id.clear_song:
                    mSearchSong.setText("");
                    mSearchSong.requestFocus();
                    mImm.showSoftInput(mSearchSong, InputMethodManager.RESULT_SHOWN);
                    break;
                default:
                    throw new RuntimeException("Any view to clear with id " + v.getId());
            }
        }
    }
}
