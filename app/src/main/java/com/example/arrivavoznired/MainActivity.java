package com.example.arrivavoznired;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {


    //Build favourites menu
    void buildFavourites(){
        favouritesBuilder.setTitle("Izberi priljubljeno povezavo");

        List<FavouriteLine> favList = new FavouriteLine("a","b").StringToList(
                sharedPref.getString("favourite_lines",""));

        final String[] favArr = new String[favList.size()];

        for (int i = 0; i < favList.size(); i++) {
            favArr[i] = favList.get(i).toString();
        }

        favouritesBuilder.setItems(favArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] chosenFav = favArr[which].split(" - ");
                input_departure.setText(chosenFav[0]);
                input_arrival.setText(chosenFav[1]);
                input_departure.clearFocus();
                input_arrival.clearFocus();
            }
        });

        favourites_dialog = favouritesBuilder.create();
    }

    //Check if current stations are valid
    boolean checkStationsValid(){
        String departure_station_name   = input_departure.getText().toString();
        String arrival_station_name     = input_arrival.getText().toString();


        String departure_station_id     = station_id_map.get(
                departure_station_name.toLowerCase());

        String arrival_station_id       = station_id_map.get(
                arrival_station_name.toLowerCase());

        if(departure_station_id!=null && arrival_station_id!=null){
            return true;
        }
        else{
            return false;
        }
    }

    //Check if line is in favourite, set favourite_button image accordingly
    boolean checkFavourite(FavouriteLine line, List<FavouriteLine> favList){
        if (favList.contains(line)){
            favourite_button.setImageResource(R.drawable.ic_favorite_black_24dp);
            System.out.println("Line is favourited.");
            return true;
        }
        else{
            favourite_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            System.out.println("Line not favourited.");
            return false;
        }
    }

    //Run checkFavourite with no parameters, current input values are considered
    boolean checkFavouriteCurrent(){
        String departure_station_name   = input_departure.getText().toString();
        String arrival_station_name     = input_arrival.getText().toString();
        FavouriteLine line = new FavouriteLine(departure_station_name,arrival_station_name);
        List<FavouriteLine> favList = line.StringToList(
                sharedPref.getString("favourite_lines",""));
        return checkFavourite(line,favList);
    }

    //Views
    AutoCompleteTextView    input_departure;
    AutoCompleteTextView    input_arrival;
    CalendarView            input_date;
    Button                  button_swap;
    Button                  button_send;
    ImageView               favourite_button;
    Toolbar                 myToolbar;
    ProgressDialog          dialog;
    AlertDialog             favourites_dialog;

    //Variables
    List<String>            autocomplete_station_list;
    Map<String,String>      station_id_map;
    AlertDialog.Builder     alertBuilder;
    AlertDialog.Builder     favouritesBuilder;
    String                  final_input_date;
    SimpleDateFormat        sdf;
    SharedPreferences       sharedPref;
    SharedPreferences.Editor prefEditor;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        input_departure = (AutoCompleteTextView)findViewById(R.id.input_departure);
        input_arrival   = (AutoCompleteTextView)findViewById(R.id.input_arrival);
        input_date      = (CalendarView)findViewById(R.id.input_date);
        button_swap     = (Button)findViewById(R.id.button_swap);
        button_send     = (Button)findViewById(R.id.button_send);
        favourite_button= (ImageView)findViewById(R.id.favourite_button);
        myToolbar       = (Toolbar)findViewById(R.id.my_toolbar_main_activity);

        //Set action bar
        setSupportActionBar(myToolbar);

        //Shared Preferences
        sharedPref      = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor      = sharedPref.edit();



        //Create favourite items dialog
        favouritesBuilder = new AlertDialog.Builder(this);
        buildFavourites();


        //Build loading alert dialog
        alertBuilder    = new AlertDialog.Builder(this);
        dialog          = new ProgressDialog(this);
        dialog.setMessage("Iščem rezultate...");

        //Date formatter and calendar updater
        sdf                 = new SimpleDateFormat("dd.MM.yyyy");
        final_input_date    = sdf.format(Calendar.getInstance().getTime());
        input_date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                final_input_date = String.format("%02d.%02d.%04d",dayOfMonth,month+1,year);
            }
        });

        //Lose focus on action done listener for arrival station input
        input_arrival.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    input_arrival.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input_arrival.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        //Clear station input fields if focused
        input_departure.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    input_departure.setText("");
                }
            }
        });

        input_arrival.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    input_arrival.setText("");
                }

            }
        });

        //Go to izstopnaPostaja when autoComplete value is selected on vstopnaPostaja
        input_departure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_arrival.requestFocus();

            }
        });

        //Close keyboard when autoComplete value is selected on izstopnaPostaja
        input_arrival.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_arrival.clearFocus();
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input_arrival.getWindowToken(), 0);


            }
        });

        //Favourite/unfavourite line button click listener
        favourite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FavouriteLine line = null;
                String departure_station_name   = input_departure.getText().toString();
                String arrival_station_name     = input_arrival.getText().toString();

                if(checkStationsValid()){
                    line = new FavouriteLine(departure_station_name,arrival_station_name);
                    List<FavouriteLine> favList = line.StringToList(
                            sharedPref.getString("favourite_lines",""));
                    if(checkFavourite(line,favList)){
                        favList.remove(line);
                        checkFavourite(line,favList);
                    }else{
                        favList.add(line);
                        checkFavourite(line,favList);
                    }
                    prefEditor.putString("favourite_lines",line.ListToString(favList));
                    prefEditor.apply();
                    buildFavourites();

                }
                else{
                    String error = "Vpisane postaje ne obstajajo.\n" +
                            "Preverite imena postaj in poskusite ponovno.";

                    alertBuilder.setMessage(error);

                    AlertDialog errorDialog = alertBuilder.create();
                    checkFavouriteCurrent();
                    errorDialog.show();

                }



            }
        });

        input_departure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkFavouriteCurrent();
            }
        });

        input_arrival.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkFavouriteCurrent();
            }
        });

        //Swap button listener
        button_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_departure.clearFocus();
                input_arrival.clearFocus();

                String departure_text   = input_departure.getText().toString();
                String arrival_text     = input_arrival.getText().toString();
                input_arrival.setText(departure_text);
                input_departure.setText(arrival_text);

                checkFavouriteCurrent();
            }
        });

        //Send button listener, calls TimetableActivity to display requested line timetable
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_departure.clearFocus();
                input_arrival.clearFocus();

                String departure_station_name   = input_departure.getText().toString();

                String arrival_station_name     = input_arrival.getText().toString();

                String departure_station_id     = station_id_map.get(
                        departure_station_name.toLowerCase());

                String arrival_station_id       = station_id_map.get(
                        arrival_station_name.toLowerCase());

                if(checkStationsValid()){
                    dialog.show();

                    if(sharedPref.getBoolean("saveLastInput",true)){
                        prefEditor.putString("lastInputDeparture",departure_station_name);
                        prefEditor.putString("lastInputArrival",arrival_station_name);
                        prefEditor.apply();
                    }

                    Intent processQueryIntent = new Intent(getApplicationContext(),
                            TimetableActivity.class);

                    processQueryIntent.putExtra("departure_station_name",
                            departure_station_name);

                    processQueryIntent.putExtra("departure_station_id",
                            departure_station_id);

                    processQueryIntent.putExtra("arrival_station_name",
                            arrival_station_name);

                    processQueryIntent.putExtra("arrival_station_id",
                            arrival_station_id);

                    processQueryIntent.putExtra("input_date",
                            final_input_date);
                    startActivity(processQueryIntent);
                }else{
                    String error = "Vpisane postaje ne obstajajo.\n" +
                            "Preverite imena postaj in poskusite ponovno.";

                    alertBuilder.setMessage(error);

                    AlertDialog errorDialog = alertBuilder.create();
                    errorDialog.show();
                }

            }
        });


        //Parse station xml file for list of stations and their IDs
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

        ArrayAdapter<String> input_adapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,autocomplete_station_list);

        //Set input thresholds
        input_departure.setAdapter(input_adapter);
        input_departure.setThreshold(2);
        input_arrival.setAdapter(input_adapter);
        input_arrival.setThreshold(2);

        //Set last used stations

    }

    //Hide loading dialog when activity resumes
    @Override
    public void onResume(){
        super.onResume();
        dialog.hide();
        if(sharedPref.getBoolean("saveLastInput",true)){
            input_departure.setText(sharedPref.getString("lastInputDeparture",""));
            input_arrival.setText(sharedPref.getString("lastInputArrival",""));
        }
        else{
            input_departure.setText("");
            input_arrival.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_favorites:
                favourites_dialog.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
