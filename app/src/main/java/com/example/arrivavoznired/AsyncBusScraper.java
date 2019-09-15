package com.example.arrivavoznired;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AsyncBusScraper extends AsyncTask<Void,Integer,List<Bus>> {

    private String mDepartureName;
    private String mArrivalName;
    private String mDate;
    private boolean mDontShowPastBuses;

    private BusCardViewAdapter mAdapter;
    private List<Bus> mBusList;
    private String mUrl;
    private ProgressDialog mProgressDialog;

    AsyncBusScraper(
            String departureName,
            String departureId,
            String arrivalName,
            String arrivalId,
            String date,
            boolean dontShowPastBuses,
            List<Bus> busList,
            BusCardViewAdapter adapter,
            ProgressDialog progressDialog
            ){
        mDepartureName = departureName;
        mArrivalName = arrivalName;
        mDate = date;
        mDontShowPastBuses = dontShowPastBuses;
        mBusList = busList;
        mAdapter = adapter;
        mProgressDialog = progressDialog;


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
                mDate);
    }

    @Override
    protected void onPostExecute(List<Bus> buses) {
        super.onPostExecute(buses);
        mAdapter.notifyDataSetChanged();
        mProgressDialog.dismiss();
    }

    @Override
    protected List<Bus> doInBackground(Void... voids) {

        try{
            Calendar curTime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String curDate = sdf.format(curTime.getTime());
            int curHour = curTime.get(Calendar.HOUR_OF_DAY);
            int curMin = curTime.get(Calendar.MINUTE);

            Document doc = Jsoup.connect(this.mUrl).get();

            Element connections = doc.getElementsByClass("connections").first();

            int i=0;
            for(Element connection:connections.getElementsByClass("connection")){
                if(isCancelled()){
                    //The calling activity has been destroyer, the async task must be cancelled to prevent memory leaks
                    return null;
                }
                if(i>0){
                    //departure-arrival
                    Element departureArrival = connection.getElementsByClass("departure-arrival").first();

                    Element departure = departureArrival.getElementsByClass("departure").first();
                    String departureTime = departure.getElementsByTag("span").first().text();
                    int depHour = Integer.parseInt(departureTime.split(":")[0]);
                    int depMin = Integer.parseInt(departureTime.split(":")[1]);

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

                    if(mDontShowPastBuses && curDate.equals(this.mDate)){

                        if((depHour > curHour) || (depHour == curHour && depMin >= curMin)) {

                            mBusList.add(new Bus(departureTime, arrivalTime, price, length, duration, mDepartureName, mArrivalName));
                        }
                    }
                    else{
                        mBusList.add(new Bus(departureTime,arrivalTime,price,length,duration,mDepartureName,mArrivalName));
                    }



                }
                i++;
            }
        }
        catch (Exception e){
            Log.d("WebParser",e.getLocalizedMessage());
        }
        return mBusList;
    }


}
