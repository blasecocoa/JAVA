package com.example.nonchalantcocoa.java1d;

public class Signal {
    public boolean start;
    public boolean go_to_cuisine;
    public boolean go_to_result;

    public Signal(boolean start, boolean go_to_cuisine, boolean go_to_result) {
        this.start = start;
        this.go_to_cuisine = go_to_cuisine;
        this.go_to_result = go_to_result;
    }

    public Signal() {
        this.start = false;
        this.go_to_cuisine = true;
        this.go_to_result = true;
    }


}
