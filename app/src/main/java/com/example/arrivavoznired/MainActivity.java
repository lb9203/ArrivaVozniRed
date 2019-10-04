package com.example.arrivavoznired;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity {

    //Shared preferences keys
    static final String FAVOURITE_LINES_KEY = "favourite_lines";
    static final String LAST_INPUT_DEPARTURE_KEY = "last_input_departure";
    static final String LAST_INPUT_ARRIVAL_KEY = "last_input_arrival";
    static final String CACHE_VERSION_KEY = "cache_version";

    //Intent actions
    static final String WIDGET_LAUNCH_ACTION = "widget_launch_action";
    static final String FINISH_ACTIVITY_ACTION = "finish_activity_action";


    /**
     * Build the favourites menu from shared preferences.
     */
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
                isSwapping = true;
                inputDeparture.setText(chosenFav[0]);
                inputArrival.setText(chosenFav[1]);
                isSwapping = false;
                isFavourite = checkFavourite();
                morphFavouriteButtonDrawable();
                inputDeparture.clearFocus();
                inputArrival.clearFocus();
            }

        });

        favouritesDialog = favouritesBuilder.create();
    }


    /**
     * Check if input stations are valid (exist in the station Map).
     * Display errors on TextInputLayouts if either station is not valid.
     * @return boolean true if both stations exist, false otherwise.
     */
    boolean checkStationsValid(){
        String departureStationName   = inputDeparture.getText().toString();
        String arrivalStationName     = inputArrival.getText().toString();


        String departureStationId     = stationIdMap.get(
                departureStationName.toLowerCase());

        String arrivalStationId       = stationIdMap.get(
                arrivalStationName.toLowerCase());

        if(departureStationId == null){
            inputDepartureLayout.setError(
                    getResources().getString(R.string.departure_station_not_found_error)
            );
        }

        if(arrivalStationId == null){
            inputArrivalLayout.setError(
                    getResources().getString(R.string.arrival_station_not_found_error)
            );
        }

        return departureStationId != null && arrivalStationId != null;
    }


    /**
     * Check if a line is a favourite line.
     * @param line FavouriteLine to be checked against list of favourites.
     * @return boolean true if the provided line is currently in favourites, false otherwise.
     */
    boolean checkFavourite(FavouriteLine line){
        return favList.contains(line);
    }


    /**
     * Check if currently inputted line is among favourite lines.
     * @return boolean true if the input line is among favourites.
     */
    boolean checkFavourite(){
        FavouriteLine line = new FavouriteLine(inputDeparture.getText().toString(),
                inputArrival.getText().toString()
        );
        return checkFavourite(line);
    }


    /**
     * Morph the favourite button depending on if the current stations are favourites.
     */
    void morphFavouriteButtonDrawable(){
        AnimatedVectorDrawableCompat newDrawable = isFavourite ? addToRemove : removeToAdd;
        String contentDescription = isFavourite ?
                getResources().getString(R.string.favourite_button_remove_description):
                getResources().getString(R.string.favourite_button_add_description);
        if (!newDrawable.equals(currentDrawable)) {
            favouriteButton.setImageDrawable(newDrawable);
            favouriteButton.setContentDescription(contentDescription);
            newDrawable.start();
            currentDrawable = newDrawable;
        }
    }


    /**
     * Initialize the cache, invalidating it if the cache is older than the current day.
     */
    void initializeCache(){
        Calendar curCal = Calendar.getInstance();

        SimpleDateFormat cacheDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String curDate = cacheDateFormat.format(curCal.getTime());
        String cacheVersion = sharedPref.getString(CACHE_VERSION_KEY,"01.01.1970");
        try {
            Date curDateDate = cacheDateFormat.parse(curDate);
            Date cacheVersionDate = cacheDateFormat.parse(cacheVersion);
            if(cacheVersionDate.before(curDateDate)){
                Log.d("initializeCache:",cacheVersion+" is before "+curDate+", invalidating.");
                BusCache.invalidateCache(MainActivity.this.getCacheDir()+BusCache.CACHE_FILENAME);
                prefEditor.putString(CACHE_VERSION_KEY,curDate);
            }
            else{
                Log.d("initializeCache:",cacheVersion+" is not before "+curDate+", loading.");
                BusCache.loadCache(new ReaderPackage(MainActivity.this.getCacheDir()+BusCache.CACHE_FILENAME));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    //Input views
    AutoCompleteTextView    inputDeparture;
    AutoCompleteTextView    inputArrival;
    TextInputLayout         inputDepartureLayout;
    TextInputLayout         inputArrivalLayout;
    TextView                inputDate;
    DatePickerDialog        inputDateDialog;

    //Utility views
    Button                  buttonSwap;
    Button                  buttonSend;
    ImageView               favouriteButton;
    Toolbar                 myToolbar;
    AlertDialog             favouritesDialog;
    LinearLayout            containerLayoutFocusable;

    //Variables
    String[]                    autocompleteStationArray;
    Map<String,String>          stationIdMap;
    MaterialAlertDialogBuilder  favouritesBuilder;
    SimpleDateFormat            sdf;
    SharedPreferences           sharedPref;
    SharedPreferences.Editor    prefEditor;
    AppWidgetManager            appWidgetManager;
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
        inputDeparture              = findViewById(R.id.input_departure);
        inputArrival                = findViewById(R.id.input_arrival);
        inputDepartureLayout        = findViewById(R.id.input_departure_layout);
        inputArrivalLayout          = findViewById(R.id.input_arrival_layout);
        inputDate                   = findViewById(R.id.input_date);
        buttonSwap                  = findViewById(R.id.button_swap);
        buttonSend                  = findViewById(R.id.button_send);
        favouriteButton             = findViewById(R.id.favourite_button);
        myToolbar                   = findViewById(R.id.my_toolbar_main_activity);
        containerLayoutFocusable    = findViewById(R.id.container_layout_focusable);

        //Favourite transition animations
        addToRemove = AnimatedVectorDrawableCompat.create(
                this,R.drawable.heart_add_to_remove_anim_rotation
        );

        removeToAdd = AnimatedVectorDrawableCompat.create(
                this,R.drawable.heart_remove_to_add_anim_rotation
        );

        //Set action bar
        setSupportActionBar(myToolbar);

        //Shared Preferences
        sharedPref      = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor      = sharedPref.edit();

        favList = FavouriteLine.stringToList(sharedPref.getString(FAVOURITE_LINES_KEY,""));


        appWidgetManager = AppWidgetManager.getInstance(this);

        isFavourite = checkFavourite();
        isSwapping = false;
        morphFavouriteButtonDrawable();
        //Create favourite items dialog
        favouritesBuilder = new MaterialAlertDialogBuilder(this);
        buildFavourites();

        //Date formatter and calendar updater
        sdf                         = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar calendar     = Calendar.getInstance();

        inputDate.setText(sdf.format(calendar.getTime()));


        inputDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                inputDate.setText(sdf.format(calendar.getTime()));
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));



        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDateDialog.show();
            }
        });

        //Lose focus on action done listener for arrival station input
        inputArrival.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    containerLayoutFocusable.requestFocus();
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
                if (sharedPref.getBoolean(SettingsActivity.ERASE_INPUT_ON_CLICK_KEY,false)
                        && hasFocus
                ) {
                    inputDeparture.setText("");
                }
                inputDepartureLayout.setError(null);
            }
        });

        //Clear text of inputArrival when focused
        inputArrival.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (sharedPref.getBoolean(SettingsActivity.ERASE_INPUT_ON_CLICK_KEY,false)
                        && hasFocus
                ) {
                    inputArrival.setText("");
                }
                inputArrivalLayout.setError(null);
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
                containerLayoutFocusable.requestFocus();
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputArrival.getWindowToken(), 0);


            }
        });

        //Favourite/unfavourite line button click listener
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkStationsValid()){
                    return;
                }

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

                ComponentName favouritesWidgetName = new ComponentName(getApplicationContext(),FavouritesWidget.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(favouritesWidgetName);

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.favourites_list);

            }
        });

        //Update favourite status if text changed
        inputDeparture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Empty because it has to be overridden.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Empty because it has to be overridden.
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isSwapping){
                    isFavourite = checkFavourite();
                    morphFavouriteButtonDrawable();
                }

            }
        });

        //Update favourite status if text changed
        inputArrival.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Empty because it has to be overridden.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Empty because it has to be overridden.
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
                            inputDate.getText().toString());
                    startActivity(processQueryIntent);
                }

            }
        });


        //Parse station xml file for list of stations and their IDs
        stationIdMap = new HashMap<>();
        String [] stationArray      = getResources().getStringArray(R.array.stations);
        autocompleteStationArray    = new String[stationArray.length];

        for(int i=0;i<stationArray.length;i++){
            String[] splitStation = stationArray[i].split(",");
            String stationName = splitStation[0];
            String stationID = splitStation[1];
            autocompleteStationArray[i] = stationName;
            stationIdMap.put(stationName.toLowerCase(),stationID);
        }

        ArrayAdapter<String> inputAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_menu_item, autocompleteStationArray);

        //Set input thresholds
        inputDeparture.setAdapter(inputAdapter);
        inputDeparture.setThreshold(2);
        inputArrival.setAdapter(inputAdapter);
        inputArrival.setThreshold(2);


        //Set last used stations

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onResume(){
        super.onResume();

        initializeCache();

        Intent mainActivityIntent = getIntent();

        assert mainActivityIntent.getAction() != null;

        switch (mainActivityIntent.getAction()) {
            case WIDGET_LAUNCH_ACTION:
                inputDeparture.setText(mainActivityIntent.getStringExtra(FavouritesWidget.PRESET_DEPARTURE_STATION_KEY));
                inputArrival.setText(mainActivityIntent.getStringExtra(FavouritesWidget.PRESET_ARRIVAL_STATION_KEY));
                buttonSend.callOnClick();
                finish();
                break;
            case FINISH_ACTIVITY_ACTION:
                finish();
                break;
            default:
                if (sharedPref.getBoolean(SettingsActivity.SAVE_LAST_INPUT_KEY, true)) {
                    inputDeparture.setText(sharedPref.getString(LAST_INPUT_DEPARTURE_KEY, ""));
                    inputArrival.setText(sharedPref.getString(LAST_INPUT_ARRIVAL_KEY, ""));
                } else {
                    inputDeparture.setText("");
                    inputArrival.setText("");
                }
                break;
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
    public void onBackPressed() {
        super.onBackPressed();
        containerLayoutFocusable.requestFocus();
    }

}
