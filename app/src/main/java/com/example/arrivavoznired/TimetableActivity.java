package com.example.arrivavoznired;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimetableActivity extends AppCompatActivity {

    Toolbar toolbarTimetableActivity;
    RecyclerView busRecycler;
    AsyncBusScraper asyncTimetableGetter;
    ArrayList<Bus> busList;
    ProgressDialog loadingBusesDialog;

    String departureStationName;
    String arrivalStationName;
    String departureStationId;
    String arrivalStationId;
    String inputDate;
    String cacheKey;

    boolean dontShowPastBuses;

    BusCardViewAdapter adapter;

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


        //Set toolbar
        setSupportActionBar(toolbarTimetableActivity);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.timetable_title));
        }

        //Get all input parameters
        departureStationName   = extra.getString("departure_station_name");
        arrivalStationName     = extra.getString("arrival_station_name");
        departureStationId     = extra.getString("departure_station_id");
        arrivalStationId       = extra.getString("arrival_station_id");
        inputDate              = extra.getString("input_date");

        cacheKey = departureStationId+"-"+arrivalStationId+"-"+inputDate;

        //Assert that inputs aren't null
        assert departureStationName != null;
        assert arrivalStationName   != null;

        busList = new ArrayList<>();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this);

        busRecycler.setLayoutManager(layoutManager);

        adapter = new BusCardViewAdapter(busList,this);

        busRecycler.setAdapter(adapter);

        dontShowPastBuses = SettingsActivity.arePastBusesShown(TimetableActivity.this);

        if (BusCache.contains(cacheKey)){
            busList.addAll(BusCache.getBusListFromCache(cacheKey));
            displayBusesInRecycler(busList);
        }
        else {
            asyncTimetableGetter = new AsyncBusScraper(
                    departureStationName,
                    departureStationId,
                    arrivalStationName,
                    arrivalStationId,
                    inputDate,
                    this);
            asyncTimetableGetter.execute();
        }
    }

    public void displayBusesInRecycler(ArrayList<Bus> buses){
        if(!buses.isEmpty()){
            BusCache.putBusListIntoCache(cacheKey,buses);
        }
        busList.addAll(buses);
        displayBusesInRecycler();
    }

    public void displayBusesInRecycler(){
        Calendar curTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String curDate = sdf.format(curTime.getTime());
        int curHour = curTime.get(Calendar.HOUR_OF_DAY);
        int curMin = curTime.get(Calendar.MINUTE);
        for (int i = 0; i < busList.size(); i++) {
            System.out.println("Bus");
            if(dontShowPastBuses && curDate.equals(inputDate)){
                int depHour = Integer.parseInt(busList.get(i).departureTime.split(":")[0]);
                int depMin = Integer.parseInt(busList.get(i).departureTime.split(":")[1]);
                if( (depHour < curHour) || (depHour == curHour && depMin < curMin ) ) {
                    busList.remove(i--);
                }
            }
        }
        System.out.println("Done");
        adapter.notifyDataSetChanged();
        for (Bus a:
             busList) {
            System.out.println(a.departureTime);
        }
        loadingBusesDialog.dismiss();
    }

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
    protected void onDestroy() {
        super.onDestroy();
        if(asyncTimetableGetter!=null){
            asyncTimetableGetter.cancel(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusCache.saveCache(new WriterPackage(TimetableActivity.this.getCacheDir()+BusCache.CACHE_FILENAME,BusCache.getBusCache()));
    }
}
