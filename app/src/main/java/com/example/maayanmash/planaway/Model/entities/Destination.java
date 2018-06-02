package com.example.maayanmash.planaway.Model.entities;

import com.example.maayanmash.planaway.Model.Constants;

public class Destination {
    private String dID;
    private String mID;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public Destination(String dID, String mID,String name, String address, double latitude, double longitude) {
        this.dID = dID;
        this.mID = mID;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Destination (String dID, Double latitude, Double longitude){
        this.dID = dID;
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

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Destination{" +
                "dID='" + dID + '\'' +
                ", mID='" + mID + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public boolean isCloseEnough(Destination address) {
        Double latDiff = (this.latitude - address.latitude);
        latDiff *= latDiff;
        latDiff *= 10000000;
        Double longDiff = (this.longitude - address.longitude);
        longDiff *= longDiff;
        longDiff *= 10000000;

        Double diff = (latDiff + longDiff);
        if(diff <= Constants.MAX_DIFF_FOR_DESTINATION_ARRIVAL)
            return true;
        else
            return false;
    }
}
