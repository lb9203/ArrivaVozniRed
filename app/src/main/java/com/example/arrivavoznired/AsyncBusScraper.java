package com.example.arrivavoznired;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class AsyncBusScraper extends AsyncTask<Void,Bus,ArrayList<Bus>> {

    private String buildDisplayPathUrl(String dataArgs){
        return  "https://arriva.si/wp-admin/admin-ajax.php?action=get_DepartureStationList&"+
                dataArgs.substring(1,dataArgs.length()-1).
                        replace("\"","").
                        replace(":","=").
                        replace(",","&").
                        replace(" ","+");
    }

    private static final String TAG = AsyncBusScraper.class.getSimpleName();

    private String mDepartureName;
    private String mArrivalName;
    private String mUrl;

    private WeakReference<TimetableActivity> timetableActivityWeakReference;

    AsyncBusScraper(
            String departureName,
            String departureId,
            String arrivalName,
            String arrivalId,
            String date,
            TimetableActivity timetableActivity
            ){
        mDepartureName = departureName;
        mArrivalName = arrivalName;

        mUrl = String.format("https://arriva.si/vozni-redi/" +
                        "?departure_id=%s" +
                        "&departure=%s" +
                        "&destination=%s" +
                        "&destination_id=%s" +
                        "&trip_date=%s",
                departureId,
                mDepartureName.replace(" ","+"),
                mArrivalName.replace(" ","+"),
                arrivalId,
                date);


        timetableActivityWeakReference = new WeakReference<>(timetableActivity);
    }

    @Override
    protected void onPostExecute(ArrayList<Bus> buses) {
        super.onPostExecute(buses);
        timetableActivityWeakReference.get().displayBusesInRecycler(buses);

    }

    @Override
    protected void onProgressUpdate(Bus... values) {
        super.onProgressUpdate(values);
        for (Bus bus : values) {
            AsyncPathScraper asyncPathScraper = new AsyncPathScraper(bus);
            asyncPathScraper.execute();
        }

    }

    @Override
    protected ArrayList<Bus> doInBackground(Void... voids) {
        ArrayList<Bus> retBusList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(this.mUrl).get();

            Element connections = doc.getElementsByClass("connections").first();

            int i=0;
            for(Element connection:connections.getElementsByClass("connection")){
                if(isCancelled()){
                    //The calling activity has been destroyer, the async task must be cancelled to prevent memory leaks
                    return null;
                }
                if(i>0){

                    //Display path url
                    Element displayPath = connection.getElementsByClass("display-path").first();
                    String dataArgs = displayPath.attr("data-args");
                    String displayPathUrl = buildDisplayPathUrl(dataArgs);

                    //departure-arrival
                    Element departureArrival = connection.getElementsByClass("departure-arrival").first();

                    Element departure = departureArrival.getElementsByClass("departure").first();
                    String departureTime = departure.getElementsByTag("span").first().text();

                    Element arrival = departureArrival.getElementsByClass("arrival").first();
                    String arrivalTime = arrival.getElementsByTag("span").first().text();

                    //duration
                    Element durationDiv = connection.getElementsByClass("duration").first();
                    String duration = durationDiv.getElementsByTag("span").first().text();
                    String[] durationArr = duration.split(":");
                    duration = String.format(Locale.getDefault(),"%d min",(Integer.parseInt(durationArr[0])*60)+(Integer.parseInt(durationArr[1])));

                    //length
                    String length = connection.getElementsByClass("length").first().text();

                    //price
                    String price = connection.getElementsByClass("price").first().text();

                    //Create new bus from scraped parameters
                    Bus newBus = new Bus(departureTime,arrivalTime,mDepartureName,mArrivalName,price,length,duration,displayPathUrl);

                    //Start scraper for bus path
                    publishProgress(newBus);

                    //Add bus to list
                    retBusList.add(newBus);
                }
                i++;
            }
        }
        catch (Exception e){
            Log.d(TAG,e.getLocalizedMessage());
        }
        return retBusList;
    }


}
