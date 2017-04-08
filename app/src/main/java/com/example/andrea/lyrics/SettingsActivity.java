package com.example.andrea.lyrics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Switch autoSearchSwitch = (Switch) findViewById(R.id.auto_search_switch);
        autoSearchSwitch.setChecked(SettingsManager.isEnabled(SettingsActivity.this));
        autoSearchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.toggleEnabled(SettingsActivity.this, isChecked);
            }
        });
    }
}
