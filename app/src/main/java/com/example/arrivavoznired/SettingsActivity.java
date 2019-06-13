package com.example.arrivavoznired;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    SwitchCompat noShowPastBuses_switch;
    SwitchCompat saveLastInput_switch;

    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Nastavitve");



        noShowPastBuses_switch  = (SwitchCompat)findViewById(R.id.noShowPastBuses_switch);
        saveLastInput_switch    = (SwitchCompat)findViewById(R.id.saveLastInput_switch);

        //Shared Preferences
        sharedPref      = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor      = sharedPref.edit();

        saveLastInput_switch.setChecked(sharedPref.getBoolean("saveLastInput",true));
        noShowPastBuses_switch.setChecked(sharedPref.getBoolean("noShowPastBuses",true));

        //Change settings according to switch positions
        saveLastInput_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefEditor.putBoolean("saveLastInput",true);
                }
                else{
                    prefEditor.putBoolean("saveLastInput",false);
                }
                prefEditor.apply();
            }
        });

        noShowPastBuses_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefEditor.putBoolean("noShowPastBuses",true);
                }
                else{
                    prefEditor.putBoolean("noShowPastBuses",false);
                }
                prefEditor.apply();
            }
        });
    }
}
