package com.example.nonchalantcocoa.java1d;

import java.util.Map;

public class Host {
    public double location;
    public Map<String,Boolean> users;
    public double radius;
    public boolean start;
    public boolean go_to_cuisine;
    public boolean go_to_result;

    public Host(double location, Map<String,Boolean> users, double radius, boolean start, boolean go_to_cuisine, boolean go_to_result) {
        this.location = location;
        this.users = users;
        this.radius = radius;
        this.start = start;
        this.go_to_cuisine = go_to_cuisine;
        this.go_to_result = go_to_result;
    }

    public Host(double location, Map<String,Boolean> users, double radius) {
        this.location = location;
        this.users = users;
        this.radius = radius;
        this.start = false;
        this.go_to_cuisine = false;
        this.go_to_result = false;
    }
}
// Put a comment here to avoid                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      error: illegal character: '\u0000'