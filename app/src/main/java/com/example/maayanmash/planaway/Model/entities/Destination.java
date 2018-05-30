package com.example.maayanmash.planaway.Model.entities;

public class Destination {
    private String dID;
    private String mID;
    private String address;
    public double latitude;
    public double longitude;


    public Destination(String dID, String mID, String address, double latitude, double longitude) {
        this.dID = dID;
        this.mID = mID;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
