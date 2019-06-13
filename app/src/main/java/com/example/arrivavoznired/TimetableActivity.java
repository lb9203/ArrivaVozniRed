package com.example.arrivavoznired;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toolbar;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
                arrival_station_name,arrival_station_id,input_date,this);
        List<Bus> busList = wp.fetchData();

        RecyclerView bus_recycler = (RecyclerView)findViewById(R.id.bus_recycler);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);
        bus_recycler.setLayoutManager(layoutManager);
        BusCardViewAdapter adapter = new BusCardViewAdapter(busList, this);
        bus_recycler.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
