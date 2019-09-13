package com.example.arrivavoznired;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Intent intent   = getIntent();
        Bundle extra    = intent.getExtras();
        assert extra != null;

        String departureStationName   = extra.getString("departure_station_name");
        String arrivalStationName     = extra.getString("arrival_station_name");
        String departureStationId     = extra.getString("departure_station_id");
        String arrivalStationId       = extra.getString("arrival_station_id");
        String inputDate              = extra.getString("input_date");

        assert departureStationName != null;
        assert arrivalStationName   != null;

        WebParser wp = new WebParser(departureStationName,departureStationId,
                arrivalStationName,arrivalStationId,inputDate,this);
        List<Bus> busList = wp.fetchData();

        RecyclerView busRecycler = findViewById(R.id.bus_recycler);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        busRecycler.setLayoutManager(layoutManager);
        BusCardViewAdapter adapter = new BusCardViewAdapter(busList, this);
        busRecycler.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
