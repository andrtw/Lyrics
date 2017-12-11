package com.example.andrea.lyrics.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrea.lyrics.R;
import com.example.andrea.lyrics.model.Lyrics;

import java.util.ArrayList;
import java.util.List;

public class LyricsFragment extends Fragment {

    private static final String ARG_LYRICS = "lyrics";

    private OnLyricsFragmentListener mListener;

    private Lyrics mLyrics;

    private LinearLayout mLyricsLines;

    private List<TextView> mSelectedLines;

    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.lyrics_action_mode, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Return false if nothing is done
            return false;
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.lyrics_action_mode_copy:
                    // build string to copy based on the selected lines
                    StringBuilder lines = new StringBuilder();
                    for (int i = 0; i < mSelectedLines.size(); i++) {
                        lines.append(mSelectedLines.get(i).getText().toString());
                        if (i != mSelectedLines.size() - 1) {
                            lines.append("\n");
                        }
                    }
                    copyToClipboard(lines.toString());

                    // Action picked, so close the ActionMode
                    mode.finish();
                    return true;
                case R.id.lyrics_action_mode_select_all:
                    mSelectedLines.clear();
                    for (int i = 0; i < mLyricsLines.getChildCount(); i++) {
                        TextView line = (TextView) mLyricsLines.getChildAt(i);
                        mSelectedLines.add(line);
                        line.setBackgroundColor(getResources().getColor(R.color.lyrics_line_selected));
                    }
                    mode.setTitle("" + mLyricsLines.getChildCount());
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            for (TextView line : mSelectedLines) {
                line.setBackgroundColor(0);
            }
            mSelectedLines.clear();
            mActionMode = null;
        }
    };

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

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.lyrics_scroll_view);
        TextView artistSongText = (TextView) view.findViewById(R.id.artist_song_text);
        mLyricsLines = (LinearLayout) view.findViewById(R.id.lyrics_lines);

        scrollView.smoothScrollTo(0, 0);
        artistSongText.setText(mLyrics.getArtistName() + " | " + mLyrics.getSongName());

        mSelectedLines = new ArrayList<>();

        String[] lines = mLyrics.getLyrics().split("<br>");
        for (String line : lines) {
            final TextView lineTv = new TextView(getActivity());
            lineTv.setTextColor(getResources().getColor(R.color.light_gray));
            lineTv.setGravity(Gravity.CENTER);
            // lyrics may contain html syntax (italic, bold)
            lineTv.setText(Html.fromHtml(line));
            lineTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if some lines are already selected, then select
                    if (mSelectedLines.size() > 0) {
                        toggleLyricsLineSelected(lineTv);
                    }
                    // otherwise copy
                    else {
                        copyToClipboard(lineTv.getText().toString());
                    }
                }
            });
            lineTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mActionMode != null) {
                        return false;
                    }
                    // start ActionMode
                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                    toggleLyricsLineSelected(lineTv);
                    return true;
                }
            });
            mLyricsLines.addView(lineTv);
        }

        return view;
    }

    private void toggleLyricsLineSelected(TextView textView) {
        // deselect
        if (mSelectedLines.contains(textView)) {
            mSelectedLines.remove(textView);
            textView.setBackgroundColor(0);

            // close action mode is all items are deselected
            if (mSelectedLines.size() <= 0) {
                mActionMode.finish();
            }
        }
        // select
        else {
            mSelectedLines.add(textView);
            textView.setBackgroundColor(getResources().getColor(R.color.lyrics_line_selected));
        }
        // update action mode title
        if (mActionMode != null) {
            mActionMode.setTitle("" + mSelectedLines.size());
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("lyrics lines", text.trim());
        cm.setPrimaryClip(data);
        Toast.makeText(getActivity(), R.string.lyrics_lines_copied, Toast.LENGTH_SHORT).show();
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
