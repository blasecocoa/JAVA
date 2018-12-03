package com.example.nonchalantcocoa.java1d;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Shop {

    private String name;
    private String cuisine;
    private String imageID;
    private LatLng location;
    private String price;
    private String tags;

    public Shop(String name, String cuisine, String imageID, LatLng location, String price, String tags) {
        this.name = name;
        this.cuisine = cuisine;
        this.imageID = imageID;
        this.location = location;
        this.price = price;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getImageID() {
        return imageID;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getTags() {
        return tags;
    }
}
