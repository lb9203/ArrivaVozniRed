package com.example.arrivavoznired;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView    input_departure;
    AutoCompleteTextView    input_arrival;
    CalendarView            input_date;
    Button                  button_swap;
    Button                  button_send;
    List<String>            autocomplete_station_list;
    Map<String,String>      station_id_map;
    AlertDialog.Builder     alertBuilder;
    String                  final_input_date;
    SimpleDateFormat        sdf;
    ProgressDialog          dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},)
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_departure = (AutoCompleteTextView)findViewById(R.id.input_departure);
        input_arrival   = (AutoCompleteTextView)findViewById(R.id.input_arrival);
        input_date      = (CalendarView)findViewById(R.id.input_date);
        button_swap     = (Button)findViewById(R.id.button_swap);
        button_send     = (Button)findViewById(R.id.button_send);
        alertBuilder    = new AlertDialog.Builder(this);
        sdf             = new SimpleDateFormat("dd.MM.yyyy");
        dialog          = new ProgressDialog(this);
        dialog.setMessage("Iščem rezultate...");

        final_input_date = sdf.format(Calendar.getInstance().getTime());
        input_date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                final_input_date = dayOfMonth + "." + (month + 1) + "." + year;
            }
        });

        button_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_departure.clearFocus();
                input_arrival.clearFocus();

                String departure_text   = input_departure.getText().toString();
                String arrival_text     = input_arrival.getText().toString();
                input_arrival.setText(departure_text);
                input_departure.setText(arrival_text);
            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_departure.clearFocus();
                input_arrival.clearFocus();

                String departure_station_name   = input_departure.getText().toString();
                String arrival_station_name     = input_arrival.getText().toString();
                String departure_station_id     = station_id_map.get(departure_station_name.toLowerCase());
                String arrival_station_id       = station_id_map.get(arrival_station_name.toLowerCase());

                if(departure_station_id!=null && arrival_station_id!=null){
                    dialog.show();
                    Intent processQueryIntent = new Intent(getApplicationContext(), TimetableActivity.class);
                    processQueryIntent.putExtra("departure_station_name",departure_station_name);
                    processQueryIntent.putExtra("departure_station_id",departure_station_id);
                    processQueryIntent.putExtra("arrival_station_name",arrival_station_name);
                    processQueryIntent.putExtra("arrival_station_id",arrival_station_id);
                    processQueryIntent.putExtra("input_date",final_input_date);
                    startActivity(processQueryIntent);
                }else{
                    String error = "Napaka: \n";
                    if(departure_station_id==null){
                        error += "\n-Postaja \""+departure_station_name+"\" ne obstaja.";
                    }
                    if(arrival_station_id==null){
                        error += "\n-Postaja \""+arrival_station_name+"\" ne obstaja.";
                    }
                    error += "\n\nPreverite imena postaj in poskusite ponovno.";

                    alertBuilder.setMessage(error);

                    AlertDialog errorDialog = alertBuilder.create();
                    errorDialog.show();
                }

            }
        });

        autocomplete_station_list   = new ArrayList<>();
        station_id_map              = new HashMap<>();
        String [] stationArray      = getResources().getStringArray(R.array.stations);
        for(String station:stationArray){
            String[] splitStation = station.split(",");
            String stationName = splitStation[0];
            String stationID = splitStation[1];
            autocomplete_station_list.add(stationName);
            station_id_map.put(stationName.toLowerCase(),stationID);
        }
        ArrayAdapter<String> input_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,autocomplete_station_list);

        input_departure.setAdapter(input_adapter);
        input_arrival.setAdapter(input_adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        dialog.hide();

    }


}
