package com.example.nonchalantcocoa.java1d;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Shop {

    private String name;
    private LatLng location;
    private String price;
    private List tags;

    public Shop(String name, LatLng location, String price, List tags) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public List getTags() {
        return tags;
    }
}
