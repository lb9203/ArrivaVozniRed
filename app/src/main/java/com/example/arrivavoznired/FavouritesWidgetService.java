package com.example.arrivavoznired;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class FavouritesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavouritesRemoteViewsFactory(this.getApplicationContext(),intent);
    }
}

class FavouritesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private SharedPreferences sharedPref;
    private Context mContext;
    private int mAppWidgetId;
    private List<FavouriteLine> mFavouritesList;

    FavouritesRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {
        resetSharedPref();
    }

    public void resetSharedPref(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    @Override
    public void onDataSetChanged() {
        mFavouritesList = FavouriteLine.stringToList(
                sharedPref.getString(MainActivity.FAVOURITE_LINES_KEY, "")
        );
    }

    @Override
    public void onDestroy() {
        mFavouritesList.clear();
    }

    @Override
    public int getCount() {
        return mFavouritesList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews favouritesListItem = new RemoteViews(
                mContext.getPackageName(),
                R.layout.layout_widget_favourites_item_clickable);

        favouritesListItem.setTextViewText(R.id.favourite_line_clickable,
                mFavouritesList.get(i).toString()
        );

        Bundle extras = new Bundle();

        extras.putString(FavouritesWidget.PRESET_DEPARTURE_STATION_KEY,
                mFavouritesList.get(i).getDeparture()
        );

        extras.putString(FavouritesWidget.PRESET_ARRIVAL_STATION_KEY,
                mFavouritesList.get(i).getArrival()
        );

        Intent getTimetableIntent = new Intent();
        getTimetableIntent.putExtras(extras);

        favouritesListItem.setOnClickFillInIntent(R.id.favourite_line_clickable,getTimetableIntent);

        return favouritesListItem;


    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}