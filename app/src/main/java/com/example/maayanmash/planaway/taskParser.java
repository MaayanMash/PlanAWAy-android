package com.example.maayanmash.planaway;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.maayanmash.planaway.Model.ModelFirebase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class taskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

    private GoogleMap mMap;
    private LoadToast lt;
    //private PolylineOptions polylineOptions=null;

    public taskParser(GoogleMap mMap, LoadToast lt){
        this.mMap=mMap;
        this.lt=lt;
    }


    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        JSONObject jsonObject = null;
        List<List<HashMap<String, String>>> routes = null;
        Log.d("server","strings[0]- "+strings[0]);
        try {
            jsonObject = new JSONObject(strings[0]);
            DirectionsParser directionsParser = new DirectionsParser();
            routes = directionsParser.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        //Get list route and display it into the map
        ArrayList points= null;
        PolylineOptions polylineOptions=null;

        for(List<HashMap<String, String>> path :lists){
            points=new ArrayList();
            polylineOptions=new PolylineOptions();

            for (HashMap<String, String> point: path){
                double lat=Double.parseDouble(point.get("lat"));
                double lon=Double.parseDouble(point.get("lon"));

                points.add(new LatLng(lat,lon));
            }

            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLUE);
            polylineOptions.geodesic(true);

        }

        if(polylineOptions!= null){
            mMap.addPolyline(polylineOptions);
            polylineOptions.visible(true);
            lt.success();

        }else{
            lt.error();
            //Toast.makeText(getApplicationContext(),"Durection not found", Toast.LENGTH_SHORT).show();

        }

    }

    //public PolylineOptions getPolylineOptions() { return polylineOptions; }
}