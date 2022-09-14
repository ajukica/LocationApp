package com.example.locationapp;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Gps extends Application {
    private static Gps singleton;
    private List<Location> lokacije;

    public List<Location> getLokacije() {
        return lokacije;
    }

    public void setLokacije(List<Location> lokacije) {
        this.lokacije = lokacije;
    }


    public Gps getSingleton(){
        return singleton;
    }


    public void onCreate(){
        super.onCreate();
        singleton = this;
        lokacije = new ArrayList<>();
    }
}
