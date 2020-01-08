package com.example.arrivavoznired;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class FavouritesWidget extends AppWidgetProvider {

    public static final String PRESET_DEPARTURE_STATION_KEY = "preset_departure_station";
    public static final String PRESET_ARRIVAL_STATION_KEY = "preset_arrival_station";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context,FavouritesWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.layout_widget_favourites);
            views.setRemoteAdapter(R.id.favourites_list, intent);
            views.setEmptyView(R.id.favourites_list,R.id.empty_text_view);

            Intent launchAppIntent = new Intent(context, MainActivity.class);
            launchAppIntent.setAction(Intent.ACTION_MAIN);
            PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context,0,launchAppIntent,0);
            views.setOnClickPendingIntent(R.id.favourites_widget_title,launchAppPendingIntent);

            Intent getTimetableIntent = new Intent(context, MainActivity.class);
            getTimetableIntent.setAction(MainActivity.WIDGET_LAUNCH_ACTION);
            PendingIntent getTimetablePendingIntent = PendingIntent.getActivity(context,0,getTimetableIntent,0);
            views.setPendingIntentTemplate(R.id.favourites_list,getTimetablePendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

