package com.example.maayanmash.planaway;

public class MyAddress {

    public double latitude;
    public double longitude;

    public MyAddress(double latitude, double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }

    @Override
    public String toString(){
       return this.latitude+","+this.longitude;
    }


}
