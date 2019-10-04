package com.example.arrivavoznired;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    String getDeparture() {
        return departure;
    }

    String getArrival() {
        return arrival;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s - %s",this.departure,this.arrival);
    }


    /**
     * Turns a List of FavouriteLines into a String, used for storing it in sharedPrefs.
     * @param favouriteList List of favouriteLines to be turned into a string.
     * @return a String representation of the favouriteLine list.
     */
    static String listToString(List<FavouriteLine> favouriteList){
        StringBuilder favouriteString = new StringBuilder();

        for(FavouriteLine fl:favouriteList){
            favouriteString.append(fl.toString()).append(",");
        }

        return favouriteString.toString();
    }


    /**
     * Turns a String of FavouriteLines into a List, for using it after pulling it from sharedPref.
     * @param favouriteString String of favouriteLines to be turned into a List.
     * @return a List representation of the favouriteLine list.
     */
    static List<FavouriteLine> stringToList(String favouriteString){
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
