package com.example.arrivavoznired;

public class Bus {
    String departure_time;
    String arrival_time;
    String departure_name;
    String arrival_name;
    String price;
    String length;
    String duration;

    Bus(String dt, String at, String p, String l, String dr, String dn, String an){
        this.departure_time = dt;
        this.arrival_time = at;
        this.price = p;
        this.length = l;
        this.duration = dr;
        this.departure_name = dn;
        this.arrival_name = an;
    }

}
