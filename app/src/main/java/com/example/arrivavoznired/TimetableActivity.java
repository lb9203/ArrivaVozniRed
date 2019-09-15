package com.example.arrivavoznired;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class TimetableActivity extends AppCompatActivity {

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

    Toolbar toolbarTimetableActivity;
    RecyclerView busRecycler;
    AsyncBusScraper asyncTimetableGetter;
    List<Bus> busList;
    ProgressDialog loadingBusesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        loadingBusesDialog = ProgressDialog.show(this,"",getResources().getString(R.string.loading_message));

        Intent intent   = getIntent();
        Bundle extra    = intent.getExtras();
        assert extra != null;

        toolbarTimetableActivity = findViewById(R.id.my_toolar_timetable_activity);
        busRecycler = findViewById(R.id.bus_recycler);


        busList = new ArrayList<>();

        setSupportActionBar(toolbarTimetableActivity);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.timetable_title));
        }

        String departureStationName   = extra.getString("departure_station_name");
        String arrivalStationName     = extra.getString("arrival_station_name");
        String departureStationId     = extra.getString("departure_station_id");
        String arrivalStationId       = extra.getString("arrival_station_id");
        String inputDate              = extra.getString("input_date");

        assert departureStationName != null;
        assert arrivalStationName   != null;

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        busRecycler.setLayoutManager(layoutManager);
        BusCardViewAdapter adapter = new BusCardViewAdapter(busList,this);
        busRecycler.setAdapter(adapter);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dontShowPastBuses = sharedPref.getBoolean(SettingsActivity.DONT_SHOW_PAST_BUSES_KEY,true);


        asyncTimetableGetter = new AsyncBusScraper(departureStationName,departureStationId,
                arrivalStationName,arrivalStationId,inputDate,
                dontShowPastBuses,busList,adapter,loadingBusesDialog);

        asyncTimetableGetter.execute();





    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(asyncTimetableGetter!=null){
            asyncTimetableGetter.cancel(true);
        }
    }
}
