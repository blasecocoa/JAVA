package com.example.nonchalantcocoa.java1d;

public class Globals{
    private static Globals instance;

    // Global variable
    private String hostName;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setHostName(String id){
        this.hostName=id;
    }
    public String getHostName(){
        return this.hostName;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
