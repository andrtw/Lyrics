package com.example.andrea.lyrics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.andrea.lyrics.R;
import com.example.andrea.lyrics.model.RecentSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentSearchesFragment extends Fragment {

    private static final String ARG_RECENT_SEARCHES = "recent_searches";

    private OnRecentSearchesFragmentListener mListener;

    private List<RecentSearch> mRecentSearches;

    // UI
    private ScrollView mRecentSearchesLayout;
    private LinearLayout mRecentSearchesGrid;

    public static RecentSearchesFragment newInstance(List<RecentSearch> recentSearches) {
        RecentSearchesFragment fragment = new RecentSearchesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_RECENT_SEARCHES, (ArrayList<RecentSearch>) recentSearches);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecentSearches = getArguments().getParcelableArrayList(ARG_RECENT_SEARCHES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_searches, container, false);

        mRecentSearchesLayout = (ScrollView) view.findViewById(R.id.recents_layout);
        mRecentSearchesGrid = (LinearLayout) view.findViewById(R.id.recents_grid);

        populateRecentSearches(inflater);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void populateRecentSearches(LayoutInflater inflater) {
        Collections.reverse(mRecentSearches);
        mRecentSearchesGrid.removeAllViews();
        for (final RecentSearch r : mRecentSearches) {
            View rv = inflater.inflate(R.layout.recent_search, null);
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
