package com.example.nonchalantcocoa.java1d;

public class Price {
    public int maxPrice;
    public int minPrice;

    public Price(int maxPrice, int minPrice){
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

    public Price(){ // dumb Price constructor
        this.maxPrice = 0;
        this.minPrice = 0;
    }
}