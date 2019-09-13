package com.example.arrivavoznired;

class Bus {
    String departureTime;
    String arrivalTime;
    String departureStationName;
    String arrivalStationName;
    String price;
    String length;
    String duration;

    Bus(String dt, String at, String p, String l, String dr, String dn, String an){
        this.departureTime = dt;
        this.arrivalTime = at;
        this.price = p;
        this.length = l;
        this.duration = dr;
        this.departureStationName = dn;
        this.arrivalStationName = an;
    }

}
