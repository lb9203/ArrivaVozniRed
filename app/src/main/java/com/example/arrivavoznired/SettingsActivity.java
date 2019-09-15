package com.example.arrivavoznired;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {
    public static final String SAVE_LAST_INPUT_KEY = "save_last_input";
    public static final String DONT_SHOW_PAST_BUSES_KEY = "dont_show_past_buses";

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    SwitchCompat dontShowPastBusesSwitch;
    SwitchCompat saveLastInputSwitch;
    Toolbar toolbarSettingsActivity;

    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbarSettingsActivity = findViewById(R.id.my_toolbar_settings_activity);

        setSupportActionBar(toolbarSettingsActivity);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_title));
        }



        dontShowPastBusesSwitch = findViewById(R.id.noShowPastBuses_switch);
        saveLastInputSwitch = findViewById(R.id.saveLastInput_switch);

        //Shared Preferences
        sharedPref      = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor      = sharedPref.edit();

        saveLastInputSwitch.setChecked(sharedPref.getBoolean(SAVE_LAST_INPUT_KEY,true));
        dontShowPastBusesSwitch.setChecked(sharedPref.getBoolean(DONT_SHOW_PAST_BUSES_KEY,true));

        //Change settings according to switch positions
        saveLastInputSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean(SAVE_LAST_INPUT_KEY,isChecked);
                prefEditor.apply();
            }
        });

        dontShowPastBusesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean(DONT_SHOW_PAST_BUSES_KEY,isChecked);
                prefEditor.apply();
            }
        });
    }
}
