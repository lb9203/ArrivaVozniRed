package com.example.arrivavoznired;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    public static final String SAVE_LAST_INPUT_KEY = "save_last_input";
    public static final String DONT_SHOW_PAST_BUSES_KEY = "dont_show_past_buses";
    public static final String ERASE_INPUT_ON_CLICK_KEY = "erase_input_on_click";

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

    SwitchMaterial dontShowPastBusesSwitch;
    SwitchMaterial saveLastInputSwitch;
    SwitchMaterial eraseInputOnClickSwitch;
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



        dontShowPastBusesSwitch = findViewById(R.id.dont_show_past_buses_switch);
        saveLastInputSwitch     = findViewById(R.id.save_list_input_switch);
        eraseInputOnClickSwitch = findViewById(R.id.erase_input_on_click_switch);

        //Shared Preferences
        sharedPref      = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor      = sharedPref.edit();

        saveLastInputSwitch.setChecked(sharedPref.getBoolean(SAVE_LAST_INPUT_KEY,true));
        dontShowPastBusesSwitch.setChecked(sharedPref.getBoolean(DONT_SHOW_PAST_BUSES_KEY,true));
        eraseInputOnClickSwitch.setChecked(sharedPref.getBoolean(ERASE_INPUT_ON_CLICK_KEY,false));

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

        eraseInputOnClickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean(ERASE_INPUT_ON_CLICK_KEY,isChecked);
                prefEditor.apply();
            }
        });
    }
}
