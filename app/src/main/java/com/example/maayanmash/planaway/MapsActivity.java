package com.example.maayanmash.planaway;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maayanmash.planaway.Model.ModelFirebase;
import com.example.maayanmash.planaway.Model.entities.Destination;
import com.example.maayanmash.planaway.Model.entities.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<List<Address>> addressList = null;
    private Location myLocation;
    private static final int MY_LOCATION_REQUEST_CODE = 500;

    private JSONArray jsonArray = new JSONArray();
    private List<MyAddress> addressResponse = new ArrayList<>();
    private List<MyAddress> addressMarkers =new ArrayList<>();

    private final String urlServer = "http://193.106.55.167:8889/directions/api/v1.0/list";
    private View mapView;

    private String uID;
    private DefineDestinationDialogFragment destDialogFragment= new DefineDestinationDialogFragment();

    private LoadToast lt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.uID = getIntent().getExtras().getString("uid");

        //////////// FireBase /////////////
        ModelFirebase.getInstance().getMyUserDetails(uID, new GetUserDetailsCallback() {
            @Override
            public void onComplete(User user) {
                Log.d("TAG", user.toString());
            }

            @Override
            public void onFailure() {}

            @Override
            public void onDestination(ArrayList<Destination> destinations){}
        });

        ModelFirebase.getInstance().getMyDestinationsByID(uID, new GetUserDetailsCallback() {
            @Override
            public void onDestination(ArrayList<Destination> destinations) {
                Log.d("TAG",destinations.toString());
                for (Destination dest:destinations){
                    LatLng latLng = new LatLng(dest.getLatitude(),dest.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(dest.getName()));
                    addressMarkers.add(new MyAddress(dest.getLatitude(),dest.getLongitude()));

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("address", dest.getLatitude()+", "+dest.getLongitude());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure() {}
            public void onComplete(User user){}

        });

        //////////// FireBase /////////////

        this.addressList = new ArrayList<>();

        lt = new LoadToast(this)
                .setText("")
                .setTranslationY(250);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 7.0f;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //get my location
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (myLocation != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("address", myLocation.getLatitude() + ", " + myLocation.getLongitude());
                this.jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LatLng israel = new LatLng(31.046051, 34.851611999999996);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(israel, zoomLevel));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (MyAddress address:addressMarkers){
                    Log.d("TAG","address= "+address.latitude+","+address.longitude);
                    Log.d("TAG","marker= "+marker.getPosition().latitude+","+marker.getPosition().longitude);
                    if(address.latitude==marker.getPosition().latitude &&
                            address.longitude==marker.getPosition().longitude){
                        destDialogFragment.setLat(marker.getPosition().latitude);
                       destDialogFragment.setLon(marker.getPosition().longitude);
                        destDialogFragment.setMarker_name(marker.getTitle());
                        destDialogFragment.show(getFragmentManager(),"TAG");
                        return true;
                    }
                }

                Log.d("TAG", "false");
                return false;
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public void checkMyLocation(){
        if (myLocation == null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mMap.setMyLocationEnabled(true);
            //ModelFirebase.getInstance().updateMyLocation(this.uID,myLocation.getLatitude(),myLocation.getLongitude());
            addressMarkers.add(new MyAddress(myLocation.getLatitude(),myLocation.getLongitude()));

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("address", myLocation.getLatitude() + ", " + myLocation.getLongitude());
                this.jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Your location is not turned on", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void addToMap(View view) {
        float zoomLevel = 12.0f;
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> address = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                address = geocoder.getFromLocationName(location, 1);
                addressList.add(address);
                LatLng latLng = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
                addressMarkers.add(new MyAddress(address.get(0).getLatitude(),address.get(0).getLongitude()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("address", address.get(0).getLatitude() + ", " + address.get(0).getLongitude());
                this.jsonArray.put(jsonObject);

                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            locationSearch.setText("");
        }
    }

    public void getDirection2(View view) {
        String url = getRequestUrl3();
        Log.d("server", "url3- " + url);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(this.mMap,lt);
        taskRequestDirections.execute(url);
        //lt.success();
    }

    private String getRequestUrl3() {
        LatLng origin;
        LatLng dest;
        int size = this.addressResponse.size();

        //my location in an origin
        origin = new LatLng(addressResponse.get(0).latitude, addressResponse.get(0).longitude);

        //the last address in a destination
        dest = new LatLng(addressResponse.get(size - 1).latitude, addressResponse.get(size - 1).longitude);

        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String str_waypoints = "";

        //there is wayPoints
        if (size >= 3) {
            //LatLng waypoints=null;
            str_waypoints = "waypoints=optimize:false|";

            LatLng waypoints = new LatLng(addressResponse.get(1).latitude, addressResponse.get(1).longitude);
            str_waypoints += waypoints.latitude + "," + waypoints.longitude;

            for (int i = 2; i < size - 1; i++) {
                waypoints = new LatLng(addressResponse.get(i).latitude, addressResponse.get(i).longitude);
                str_waypoints += "|" + waypoints.latitude + "," + waypoints.longitude;
            }
        }
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest + "&" + str_waypoints + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    public void sendToServer(View view) {
        lt.show();
        Server server = new Server();
        server.execute("");
        //new LocationTask().execute();
    }

    public class Server extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(urlServer);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("locations", jsonArray);
                if (myLocation != null)
                    jsonParam.put("source", myLocation.getLatitude() + ", " + myLocation.getLongitude());

                if(destDialogFragment.getDestination()!=null)
                    jsonParam.put("destination",destDialogFragment.getDestination().latitude+", "+destDialogFragment.getDestination().longitude);
                else
                    jsonParam.put("destination","empty");

                Log.d("server", jsonParam.toString());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int status = conn.getResponseCode();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                String str_res = "error response";

                if (status == 200 || status == 201) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    str_res = sb.toString();
                }

                conn.disconnect();
                return str_res;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Could not connect to server";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("server", "onPostExecute- " + s);
            if (!s.equals("error response") && !s.equals("Could not connect to server")) {
                try {
                    JSONObject myResponse = new JSONObject(s);
                    JSONArray res = (JSONArray) myResponse.get("SortedLocations");

                    for (int i = 0; i < res.length(); i++) {
                        String str = (String) res.get(i);
                        String[] parts = str.split(", ");
                        addressResponse.add(new MyAddress(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
                    }

                    Log.d("server", addressResponse.toString());
                    getDirection2(mapView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else{
                lt.error();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //FireBase
    public interface GetUserDetailsCallback{
        public void onComplete(User user);
        public void onFailure();

        void onDestination(ArrayList<Destination> destinations);
    }

    //Menu
    @SuppressLint("ResourceType")
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_manu,menu);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.MyTasks:
                Log.d("TAG","My Tasks");
                return true;
            case R.id.MyAccount:
                Log.d("TAG","My Account");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /////get my location
//    public class LocationTask extends AsyncTask<Void, Void, Void> {
//        volatile boolean stop=false;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while(!stop){
//                checkMyLocation();
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void v) {
//            super.onPostExecute(v);
//
//        }
//    }






}

