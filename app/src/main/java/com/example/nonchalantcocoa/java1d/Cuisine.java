package com.example.nonchalantcocoa.java1d;

public class Cuisine {

    private String cuisineText;

    public Cuisine() {
    }

    public Cuisine(String cuisineText) {
        this.cuisineText = cuisineText;
    }

    public String getText() {
        return cuisineText;
    }

    public void setText(String cuisineText) {
        this.cuisineText = cuisineText;
    }
}
