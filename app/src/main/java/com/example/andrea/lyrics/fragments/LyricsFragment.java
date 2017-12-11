package com.example.andrea.lyrics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrea.lyrics.R;
import com.example.andrea.lyrics.model.Lyrics;
import com.example.andrea.lyrics.model.RecentSearch;
import com.example.andrea.lyrics.views.InterceptableScrollView;

import java.util.ArrayList;
import java.util.List;

public class LyricsFragment extends Fragment {

    private static final String ARG_LYRICS = "lyrics";

    private OnLyricsFragmentListener mListener;

    private Lyrics mLyrics;

    // UI
    private InterceptableScrollView mScrollView;
    private TextView mArtistSongText;
    private TextView mLyricsText;

    public static LyricsFragment newInstance(Lyrics lyrics) {
        LyricsFragment fragment = new LyricsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LYRICS, lyrics);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLyrics = getArguments().getParcelable(ARG_LYRICS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        mScrollView = (InterceptableScrollView) view.findViewById(R.id.lyrics_scroll_view);
        mArtistSongText = (TextView) view.findViewById(R.id.artist_song_text);
        mLyricsText = (TextView) view.findViewById(R.id.lyrics_text);

        mScrollView.smoothScrollTo(0, 0);
        mArtistSongText.setText(mLyrics.getArtistName() + " | " + mLyrics.getSongName());
        mLyricsText.setText(Html.fromHtml(mLyrics.getLyrics()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLyricsFragmentListener) {
            mListener = (OnLyricsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLyricsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLyricsFragmentListener {

    }
}
