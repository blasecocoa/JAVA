package com.example.nonchalantcocoa.java1d;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host {
    public LatLng location;
    public Map<String,Boolean> users;
    public double radius;
    public Signal signal;
    public List priceList;
    public List cuisineList;
    public List avaCuisineList;
    public List resultList;

    public Host(LatLng location, Map<String, Boolean> users, double radius, Signal signal,
                List priceList, List cuisineList, List avaCuisineList, List resultList) {
        this.location = location;
        this.users = users;
        this.radius = radius;
        this.signal = signal;
        this.priceList = priceList;
        this.cuisineList = cuisineList;
        this.avaCuisineList = avaCuisineList;
        this.resultList = resultList;
    }

    public Host(LatLng location, Map<String, Boolean> users, double radius) {
        this.location = location;
        this.users = users;
        this.radius = radius;
        this.signal = new Signal();

        ArrayList priceList = new ArrayList();
        priceList.add(0);

        ArrayList cuisineList = new ArrayList();
        cuisineList.add(0);

        ArrayList avaCuisineList = new ArrayList();
        avaCuisineList.add(0);

        ArrayList resultList = new ArrayList();
        resultList.add(0);

        this.priceList = priceList;
        this.cuisineList = cuisineList;
        this.avaCuisineList = avaCuisineList;
        this.resultList = resultList;
    }
}
// Put a comment here to avoid                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      error: illegal character: '\u0000'