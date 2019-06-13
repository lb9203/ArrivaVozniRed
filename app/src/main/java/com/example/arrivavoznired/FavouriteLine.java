package com.example.arrivavoznired;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FavouriteLine {
    private String departure;
    private String arrival;

    FavouriteLine(String d,String a){
        departure = d;
        arrival = a;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof FavouriteLine){
            return (this.departure).equals(((FavouriteLine) obj).departure)
                    && this.arrival.equals(((FavouriteLine) obj).arrival);
        }
        else{
            return false;
        }
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s - %s",this.departure,this.arrival);
    }


    //Function to turn favourite list into String
    public String ListToString(List<FavouriteLine> favouriteList){
        StringBuilder favouriteString = new StringBuilder();

        for(FavouriteLine fl:favouriteList){
            favouriteString.append(fl.toString()).append(",");
        }

        return favouriteString.toString();
    }

    //Function to turn favourite string into List
    public List<FavouriteLine> StringToList(String favouriteString){
        List<FavouriteLine> favouriteList = new ArrayList<>();

        String[] splitFav = favouriteString.split(",");

        for (String fav:
             splitFav) {
            String[] temp = fav.split(" - ");
            if(temp.length==2){
                FavouriteLine newLine = new FavouriteLine(temp[0],temp[1]);
                favouriteList.add(newLine);
            }
        }

        return favouriteList;
    }
}
