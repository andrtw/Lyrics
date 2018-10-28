package com.example.andrea.lyrics.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrea.lyrics.R;
import com.example.andrea.lyrics.db.DbLyrics;
import com.example.andrea.lyrics.model.RecentSearch;

import java.util.Collections;
import java.util.List;

public class RecentSearchesFragment extends Fragment {

    private OnRecentSearchesFragmentListener mListener;
    private DbLyrics db;

    // UI
    private LinearLayout mRecentSearchesGrid;

    public static RecentSearchesFragment newInstance() {
        return new RecentSearchesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_searches, container, false);

        db = new DbLyrics(getActivity());
        db.open();

        mRecentSearchesGrid = (LinearLayout) view.findViewById(R.id.recents_grid);

        populateRecentSearches();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecentSearchesFragmentListener) {
            mListener = (OnRecentSearchesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRecentSearchesFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        db.open();
    }

    @Override
    public void onPause() {
        db.close();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void populateRecentSearches() {
        List<RecentSearch> recentSearches = db.getRecentSearches();
        Collections.reverse(recentSearches);
        mRecentSearchesGrid.removeAllViews();
        for (final RecentSearch r : recentSearches) {
            @SuppressLint("InflateParams") View rv = getLayoutInflater().inflate(R.layout.recent_search, null);
            ((TextView) rv.findViewById(R.id.recent_artist_name)).setText(r.getArtistName());
            ((TextView) rv.findViewById(R.id.recent_song_name)).setText(r.getSongName());
            rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRecentSearchClick(r.getArtistName(), r.getSongName());
                }
            });
            // for some reason setting the margins in the layout file doesn't work
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int smPad = (int) getResources().getDimension(R.dimen.sm_padding);
            params.topMargin = smPad;
            params.bottomMargin = smPad;
            params.leftMargin = smPad;
            params.rightMargin = smPad;
            mRecentSearchesGrid.addView(rv, params);
        }
    }

    public interface OnRecentSearchesFragmentListener {
        void onRecentSearchClick(String artistName, String songName);
    }
}
