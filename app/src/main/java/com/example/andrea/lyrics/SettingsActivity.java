package com.example.andrea.lyrics;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.andrea.lyrics.db.DbLyrics;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Switch autoSearchSwitch = (Switch) findViewById(R.id.auto_search_switch);
        autoSearchSwitch.setChecked(SettingsManager.isAutoSearchEnabled(SettingsActivity.this));
        autoSearchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.toggleAutoSearch(SettingsActivity.this, isChecked);
            }
        });
        LinearLayout autoSearchLayout = (LinearLayout) findViewById(R.id.layout_auto_search);
        autoSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoSearchSwitch.performClick();
            }
        });

        LinearLayout deleteRecents = (LinearLayout) findViewById(R.id.layout_delete_recents);
        deleteRecents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder
                        .setTitle(R.string.dialog_delete_recents_title)
                        .setMessage(R.string.dialog_delete_recents_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DbLyrics db = new DbLyrics(SettingsActivity.this);
                                db.open();
                                db.deleteRecents();
                                db.close();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
