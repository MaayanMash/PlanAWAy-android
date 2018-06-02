package com.example.maayanmash.planaway.Model.entities;

import com.example.maayanmash.planaway.Model.Constants;

public class AddressHolder {

    public double latitude;
    public double longitude;

    public AddressHolder(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return this.latitude + "," + this.longitude;
    }

    public boolean isCloseEnough(AddressHolder address) {
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
