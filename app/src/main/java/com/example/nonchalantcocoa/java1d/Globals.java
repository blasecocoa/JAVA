package com.example.nonchalantcocoa.java1d;

public class Globals{
    private static Globals instance;

    // Global variable
    private String hostName;
    private boolean isHost;

    // Restrict the constructor from being instantiated
    private Globals(){
        hostName = "Unknown";
        isHost = false;
    }

    public void setHostName(String id){
        this.hostName=id;
    }
    public String getHostName(){
        return this.hostName;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
