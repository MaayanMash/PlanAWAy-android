package com.example.maayanmash.planaway;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.maayanmash.planaway.Model.Constants;
import com.example.maayanmash.planaway.Model.ModelFirebase;
import com.example.maayanmash.planaway.Model.entities.Destination;

import java.util.ArrayList;

public class MyService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private ArrayList<Destination> locationsList = null;

    private String uid;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        private int locationsUpdateAmount = 0;

        public LocationListener(String provider) {
            Log.d("TAG", "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            locationsUpdateAmount++;
            mLastLocation.set(location);
            Log.d("TAG", "onLocationChanged: " + location);

            if(locationsUpdateAmount >= Constants.AMOUNT_OF_LOCATIONS_UPDATE_BEFORE_UPDATE_FB){
                locationsUpdateAmount = 0;
                ModelFirebase.getInstance().updateMyLocation(location.getLatitude(), location.getLongitude());
                Destination addHolder = new Destination("",location.getLatitude(), location.getLongitude());

                if(addHolder.isCloseEnough(locationsList.get(0))){
                    Log.d("TAG","is remove "+location);
                    ModelFirebase.getInstance().updateDestinationArrivalForTask(locationsList.get(0).getdID());
                    locationsList.remove(0);
                }
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("TAG", "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("TAG", "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("TAG", "onStatusChanged: " + provider);
        }

    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String locations = intent.getExtras().getString("locations");
        String[] temp = locations.split("->");
        int len = temp.length;

        locationsList = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            String[] landl = temp[i].split(",");
            String dID= landl[0];
            Double latitude = Double.parseDouble(landl[1]);
            Double longitude = Double.parseDouble(landl[2]);
            locationsList.add(new Destination(dID,latitude,longitude));
        }

        Log.d("TAG", ">>>" + locationsList.toString());
        Log.d("TAG", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d("TAG", "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d("TAG", "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
