package com.example.arrivavoznired;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Bus implements Serializable {
    String departureTime;
    String arrivalTime;
    String departureStationName;
    String arrivalStationName;
    String price;
    String length;
    String duration;
    String displayPathUrl;
    List<String> pathList;

    Bus(String departureTime,
        String arrivalTime,
        String departureStationName,
        String arrivalStationName,
        String price,
        String length,
        String duration,
        String displayPathUrl)
    {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
        this.price = price;
        this.length = length;
        this.duration = duration;
        this.displayPathUrl = displayPathUrl;
        this.pathList = new ArrayList<>();
    }
}
