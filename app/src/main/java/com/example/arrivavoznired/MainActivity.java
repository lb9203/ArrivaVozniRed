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
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    static final String FAVOURITE_LINES_KEY = "favourite_lines";
    static final String LAST_INPUT_DEPARTURE_KEY = "last_input_departure";
    static final String LAST_INPUT_ARRIVAL_KEY = "last_input_arrival";


    //Build favourites menu
    void buildFavourites(){
        favouritesBuilder.setTitle(getResources().getString(R.string.choose_favourite));

        final String[] favArr = new String[favList.size()];

        for (int i = 0; i < favList.size(); i++) {
            favArr[i] = favList.get(i).toString();
        }

        Arrays.sort(favArr);

        favouritesBuilder.setItems(favArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] chosenFav = favArr[which].split(" - ");
                inputDeparture.setText(chosenFav[0]);
                inputArrival.setText(chosenFav[1]);
                inputDeparture.clearFocus();
                inputArrival.clearFocus();
            }
        });

        favouritesDialog = favouritesBuilder.create();
    }

    //Check if current stations are valid
    boolean checkStationsValid(){
        String departureStationName   = inputDeparture.getText().toString();
        String arrivalStationName     = inputArrival.getText().toString();


        String departureStationId     = stationIdMap.get(
                departureStationName.toLowerCase());

        String arrivalStationId       = stationIdMap.get(
                arrivalStationName.toLowerCase());

        return departureStationId != null && arrivalStationId != null;
    }


    //Check if line is in favourite, set favouriteButton image accordingly
    boolean checkFavourite(FavouriteLine line){
        return favList.contains(line);
    }

    //Run checkFavourite with no parameters, current input values are considered
    boolean checkFavourite(){

        FavouriteLine line = new FavouriteLine(inputDeparture.getText().toString(),
                inputArrival.getText().toString()
        );

        return checkFavourite(line);
    }

    void showStationsNotFoundAlert(){
        alertBuilder.setMessage(getResources().getString(R.string.stations_not_found_error));

        AlertDialog errorDialog = alertBuilder.create();
        checkFavourite();
        errorDialog.show();
    }

    void morphFavouriteButtonDrawable(){
        AnimatedVectorDrawableCompat newDrawable = isFavourite ? addToRemove : removeToAdd;
        if (!newDrawable.equals(currentDrawable)) {
            favouriteButton.setImageDrawable(newDrawable);
            newDrawable.start();
            currentDrawable = newDrawable;
        }
    }

    void setFavouriteButtonDrawable(){
        favouriteButton.setImageDrawable(isFavourite?addToRemove:removeToAdd);
    }

    //Input views
    AutoCompleteTextView    inputDeparture;
    AutoCompleteTextView    inputArrival;
    CalendarView            inputDate;

    //Utility views
    Button                  buttonSwap;
    Button                  buttonSend;
    ImageView               favouriteButton;
    Toolbar                 myToolbar;
    ProgressDialog          dialog;
    AlertDialog             favouritesDialog;

    //Variables
    List<String>                autocompleteStationList;
    Map<String,String>          stationIdMap;
    AlertDialog.Builder         alertBuilder;
    AlertDialog.Builder         favouritesBuilder;
    String                      finalInputDate;
    SimpleDateFormat            sdf;
    SharedPreferences           sharedPref;
    SharedPreferences.Editor    prefEditor;
    boolean                     isFavourite;
    boolean                     isSwapping;
    List<FavouriteLine>         favList;

    AnimatedVectorDrawableCompat addToRemove;
    AnimatedVectorDrawableCompat removeToAdd;
    AnimatedVectorDrawableCompat currentDrawable;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        inputDeparture      = findViewById(R.id.input_departure);
        inputArrival        = findViewById(R.id.input_arrival);
        inputDate           = findViewById(R.id.input_date);
        buttonSwap          = findViewById(R.id.button_swap);
        buttonSend          = findViewById(R.id.button_send);
        favouriteButton     = findViewById(R.id.favourite_button);
        myToolbar           = findViewById(R.id.my_toolbar_main_activity);

        //Favourite transition animations
        addToRemove = AnimatedVectorDrawableCompat.create(
                this,R.drawable.heart_add_to_remove_anim
        );

        removeToAdd = AnimatedVectorDrawableCompat.create(
                this,R.drawable.heart_remove_to_add_anim
        );

        //Set action bar
        setSupportActionBar(myToolbar);

        //Shared Preferences
        sharedPref      = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor      = sharedPref.edit();

        favList = FavouriteLine.stringToList(sharedPref.getString(FAVOURITE_LINES_KEY,""));

        isFavourite = checkFavourite();
        isSwapping = false;
        morphFavouriteButtonDrawable();

        //Create favourite items dialog
        favouritesBuilder = new AlertDialog.Builder(this);
        buildFavourites();


        //Build loading alert dialog
        alertBuilder    = new AlertDialog.Builder(this);
        dialog          = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.loading_message));

        //Date formatter and calendar updater
        sdf                 = new SimpleDateFormat("dd.MM.yyyy");
        finalInputDate = sdf.format(Calendar.getInstance().getTime());
        inputDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                finalInputDate = String.format("%02d.%02d.%04d",dayOfMonth,month+1,year);
            }
        });

        //Lose focus on action done listener for arrival station input
        inputArrival.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    inputArrival.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputArrival.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        //Clear station input fields if focused
        inputDeparture.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    inputDeparture.setText("");
                }
            }
        });

        inputArrival.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    inputArrival.setText("");
                }

            }
        });

        //Go to izstopnaPostaja when autoComplete value is selected on vstopnaPostaja
        inputDeparture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inputArrival.requestFocus();

            }
        });

        //Close keyboard when autoComplete value is selected on izstopnaPostaja
        inputArrival.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inputArrival.clearFocus();
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputArrival.getWindowToken(), 0);


            }
        });

        //Favourite/unfavourite line button click listener
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FavouriteLine line = new FavouriteLine(
                        inputDeparture.getText().toString(),inputArrival.getText().toString()
                );

                isFavourite = checkFavourite(line);

                if(isFavourite){
                    favList.remove(line);
                }else{
                    favList.add(line);
                }

                isFavourite = !isFavourite;

                morphFavouriteButtonDrawable();


                prefEditor.putString(FAVOURITE_LINES_KEY,FavouriteLine.listToString(favList));
                prefEditor.apply();

                buildFavourites();



            }
        });

        inputDeparture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Empty because it has to be overriden
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Empty because it has to be overriden
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isSwapping){
                    isFavourite = checkFavourite();
                    morphFavouriteButtonDrawable();
                }

            }
        });

        inputArrival.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Empty because it has to be overriden
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Empty because it has to be overriden
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isSwapping){
                    isFavourite = checkFavourite();
                    morphFavouriteButtonDrawable();
                }
            }
        });

        //Swap button listener
        buttonSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDeparture.clearFocus();
                inputArrival.clearFocus();

                String departureText   = inputDeparture.getText().toString();
                String arrivalText     = inputArrival.getText().toString();

                isSwapping = true;

                inputArrival.setText(departureText);
                inputDeparture.setText(arrivalText);

                isSwapping = false;
                isFavourite = checkFavourite();
                morphFavouriteButtonDrawable();

            }
        });

        //Send button listener, calls TimetableActivity to display requested line timetable
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDeparture.clearFocus();
                inputArrival.clearFocus();

                String departureStationName   = inputDeparture.getText().toString();

                String arrivalStationName     = inputArrival.getText().toString();

                String departureStationId     = stationIdMap.get(
                        departureStationName.toLowerCase());

                String arrivalStationId       = stationIdMap.get(
                        arrivalStationName.toLowerCase());

                if(checkStationsValid()){
                    dialog.show();

                    if(sharedPref.getBoolean(SettingsActivity.SAVE_LAST_INPUT_KEY,true)){
                        prefEditor.putString(LAST_INPUT_DEPARTURE_KEY,departureStationName);
                        prefEditor.putString(LAST_INPUT_ARRIVAL_KEY,arrivalStationName);
                        prefEditor.apply();
                    }

                    Intent processQueryIntent = new Intent(getApplicationContext(),
                            TimetableActivity.class);

                    processQueryIntent.putExtra("departure_station_name",
                            departureStationName);

                    processQueryIntent.putExtra("departure_station_id",
                            departureStationId);

                    processQueryIntent.putExtra("arrival_station_name",
                            arrivalStationName);

                    processQueryIntent.putExtra("arrival_station_id",
                            arrivalStationId);

                    processQueryIntent.putExtra("input_date",
                            finalInputDate);
                    startActivity(processQueryIntent);
                }else{
                    showStationsNotFoundAlert();
                }

            }
        });


        //Parse station xml file for list of stations and their IDs
        autocompleteStationList = new ArrayList<>();
        stationIdMap = new HashMap<>();
        String [] stationArray      = getResources().getStringArray(R.array.stations);

        for(String station:stationArray){
            String[] splitStation = station.split(",");
            String stationName = splitStation[0];
            String stationID = splitStation[1];
            autocompleteStationList.add(stationName);
            stationIdMap.put(stationName.toLowerCase(),stationID);
        }

        ArrayAdapter<String> inputAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, autocompleteStationList);

        //Set input thresholds
        inputDeparture.setAdapter(inputAdapter);
        inputDeparture.setThreshold(2);
        inputArrival.setAdapter(inputAdapter);
        inputArrival.setThreshold(2);

        //Set last used stations

    }

    //Hide loading dialog when activity resumes
    @Override
    public void onResume(){
        super.onResume();
        dialog.dismiss();
        if(sharedPref.getBoolean(SettingsActivity.SAVE_LAST_INPUT_KEY,true)){
            inputDeparture.setText(sharedPref.getString(LAST_INPUT_DEPARTURE_KEY,""));
            inputArrival.setText(sharedPref.getString(LAST_INPUT_ARRIVAL_KEY,""));
        }
        else{
            inputDeparture.setText("");
            inputArrival.setText("");
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
                favouritesDialog.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
