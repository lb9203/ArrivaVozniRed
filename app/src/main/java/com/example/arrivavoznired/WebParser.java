package com.example.arrivavoznired;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

class WebParser {
    private String departurename;
    private String arrivalname;
    private String date;
    private String url;
    private Context context;

    WebParser(String dname, String did, String aname, String aid, String date, Context con){
        departurename = dname;
        arrivalname = aname;
        this.date = date;
        this.context = con;
        url = String.format("https://arriva.si/vozni-redi/" +
                "?departure_id=%s" +
                "&departure=%s" +
                "&destination=%s" +
                "&destination_id=%s" +
                "&trip_date=%s",
                did,
                dname.replace(" ","+"),
                aname.replace(" ","+"),
                aid,
                date);
    }

    List<Bus> fetchData(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean noShowPastBuses = sharedPref.getBoolean("noShowPastBuses",true);
        List<Bus> retList = new ArrayList<>();

        try{
            Calendar curTime = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String curDate = sdf.format(curTime.getTime());
            int curHour = curTime.get(Calendar.HOUR_OF_DAY);
            int curMin = curTime.get(Calendar.MINUTE);

            Document doc = Jsoup.connect(this.url).get();

            Element connections = doc.getElementsByClass("connections").first();

            int i=0;
            for(Element connection:connections.getElementsByClass("connection")){
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
                    if(noShowPastBuses && curDate.equals(this.date)){

                        if((depHour > curHour) || (depHour == curHour && depMin >= curMin)) {

                            retList.add(new Bus(departureTime, arrivalTime, price, length, duration, this.departurename, this.arrivalname));
                        }
                    }
                    else{
                        retList.add(new Bus(departureTime,arrivalTime,price,length,duration,this.departurename,this.arrivalname));
                    }


                }
                i++;
            }
        }
        catch (Exception e){
            Log.d("WebParser",e.getLocalizedMessage());
        }
        return retList;
    }


}
