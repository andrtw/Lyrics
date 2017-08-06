package com.example.andrea.lyrics;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.andrea.lyrics.db.DbLyrics;
import com.example.andrea.lyrics.model.AutoCompleteItem;

import java.util.List;

/**
 * Created by andrea on 31/03/17.
 */

public class AutoCompleteAdapter extends ArrayAdapter<AutoCompleteItem> {

    private DbLyrics db;
    private AutocompleteListener listener;

    public interface AutocompleteListener {
        void onDelete();
    }

    public AutoCompleteAdapter(Context context, List<AutoCompleteItem> objects, DbLyrics db, AutocompleteListener listener) {
        super(context, 0, objects);
        this.db = db;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final AutoCompleteItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.item);
        name.setText(item.getName());
        TextView delete = (TextView) convertView.findViewById(R.id.item_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setTitle(R.string.delete_autocomplete_title)
                        .setMessage(R.string.delete_autocomplete_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (item.getType()) {
                                    case AutoCompleteItem.TYPE_ARTIST:
                                        db.deleteArtist(item);
                                        listener.onDelete();
                                        break;
                                    case AutoCompleteItem.TYPE_SONG:
                                        db.deleteSong(item);
                                        listener.onDelete();
                                        break;
                                    default:
                                        throw new RuntimeException("Unknown type" + item.getType());
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }
}
