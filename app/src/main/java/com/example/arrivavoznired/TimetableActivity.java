package com.example.arrivavoznired;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.util.List;

public class TimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Intent intent   = getIntent();
        Bundle extra    = intent.getExtras();
        assert extra != null;
        String departure_station_name   = extra.getString("departure_station_name");
        String arrival_station_name     = extra.getString("arrival_station_name");
        String departure_station_id     = extra.getString("departure_station_id");
        String arrival_station_id       = extra.getString("arrival_station_id");
        String input_date               = extra.getString("input_date");

        WebParser wp = new WebParser(departure_station_name,departure_station_id,
                arrival_station_name,arrival_station_id,input_date);
        List<Bus> busList = wp.fetchData();

        RecyclerView bus_recycler = (RecyclerView)findViewById(R.id.bus_recycler);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        bus_recycler.setLayoutManager(layoutManager);
        BusCardViewAdapter adapter = new BusCardViewAdapter(busList, this);
        bus_recycler.setAdapter(adapter);


    }
}
