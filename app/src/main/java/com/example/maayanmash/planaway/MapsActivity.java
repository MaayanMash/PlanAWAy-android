package com.example.maayanmash.planaway;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maayanmash.planaway.Model.Constants;
import com.example.maayanmash.planaway.Model.ModelFirebase;
import com.example.maayanmash.planaway.Model.entities.Destination;
import com.example.maayanmash.planaway.Model.entities.AddressHolder;
import com.example.maayanmash.planaway.Model.entities.TaskRow;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<List<Address>> addressList = null;
    private Location myLocation;
    private static final int MY_LOCATION_REQUEST_CODE = 500;
    private View mapView;
    private Intent serviceIntent = null;
    private JSONArray jsonArray = new JSONArray();
    private List<AddressHolder> addressResponse = new ArrayList<>();
    private List<AddressHolder> addressMarkers = new ArrayList<>();
    private List<Destination> destinationsList;
    private Map<String, String> mapDestination = new HashMap<>();
    private String uID;
    private String todayTaskID = null;
    private DefineDestinationDialogFragment destDialogFragment = new DefineDestinationDialogFragment();
    private LoadToast lt;

    private Double zoom_lat;
    private Double zoom_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        this.uID = getIntent().getExtras().getString("uid");

        //////////// FireBase /////////////
        ModelFirebase.getInstance().getMyDestinationsByID(uID, new GetDestinationsForUserIDCallback() {
            @Override
            public void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList) {
                destinationsList = destinations;
                todayTaskID = taskID;
                Double sum_lat = 0.0;
                Double sum_long = 0.0;
                float zoomLevel = 10;
                Log.d("TAG", destinations.toString());
                for (Destination dest : destinations) {
                    LatLng latLng = new LatLng(dest.getLatitude(), dest.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(dest.getName()));
                    addressMarkers.add(new AddressHolder(dest.getLatitude(), dest.getLongitude()));
                    sum_lat += dest.getLatitude();
                    sum_long += dest.getLongitude();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String latLng_str = dest.getLatitude() + ", " + dest.getLongitude();
                        jsonObject.put("address", latLng_str);
                        jsonArray.put(jsonObject);
                        mapDestination.put(latLng_str, dest.getdID());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                zoom_lat = sum_lat / destinations.size();
                zoom_long = sum_long / destinations.size();
                LatLng new_zoom = new LatLng(zoom_lat, zoom_long);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_zoom, zoomLevel));

            }
        });

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
    public void onBackPressed() {
        //super.onBackPressed();
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
            ModelFirebase.getInstance().updateMyLocation(myLocation.getLatitude(), myLocation.getLongitude());
            try {
                jsonObject.put("address", myLocation.getLatitude() + ", " + myLocation.getLongitude());
                this.jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LatLng israel = new LatLng(31.046051, 34.851611999999996);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(israel, zoomLevel));
        /*
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (AddressHolder address : addressMarkers) {
                    Log.d("TAG", "address= " + address.latitude + "," + address.longitude);
                    Log.d("TAG", "marker= " + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                    if (address.latitude == marker.getPosition().latitude &&
                            address.longitude == marker.getPosition().longitude) {
                        destDialogFragment.setLat(marker.getPosition().latitude);
                        destDialogFragment.setLon(marker.getPosition().longitude);
                        destDialogFragment.setMarker_name(marker.getTitle());
                        destDialogFragment.show(getFragmentManager(), "TAG");
                        return true;
                    }
                }

                Log.d("TAG", "false");

                return false;
            }
        });*/

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

    public void checkMyLocation() {
        if (myLocation == null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mMap.setMyLocationEnabled(true);
            //ModelFirebase.getInstance().updateMyLocation(this.uID,myLocation.getLatitude(),myLocation.getLongitude());
            //addressMarkers.add(new AddressHolder(myLocation.getLatitude(), myLocation.getLongitude()));

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
                addressMarkers.add(new AddressHolder(address.get(0).getLatitude(), address.get(0).getLongitude()));
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
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(this.mMap, lt);
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
    }

    public void startLocationService(List<AddressHolder> locationsList) {
        StringBuilder builder = new StringBuilder("");
        int size = locationsList.size();
        for (int i = 1; i < size - 1; i++) {
            builder.append(mapDestination.get(locationsList.get(i).latitude + ", " + locationsList.get(i).longitude) + ",");
            builder.append(locationsList.get(i).latitude + "," + locationsList.get(i).longitude + "->");

        }
        builder.append(mapDestination.get(locationsList.get(size - 1).latitude + ", " + locationsList.get(size - 1).longitude) + ",");
        builder.append(locationsList.get(size - 1).latitude + "," + locationsList.get(size - 1).longitude);

        Intent intent = new Intent(getBaseContext(), MyService.class);
        intent.putExtra("locations", builder.toString());
        serviceIntent = intent;
        startService(intent);
    }

    public class Server extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Constants.SEARCHING_SERVER_URL);
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

                if (destDialogFragment.getDestination() != null)
                    jsonParam.put("destination", destDialogFragment.getDestination().latitude + ", " + destDialogFragment.getDestination().longitude);
                else
                    jsonParam.put("destination", "empty");

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
                        addressResponse.add(new AddressHolder(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
                    }

                    Log.d("server", addressResponse.toString());
                    getDirection2(mapView);


                    startLocationService(addressResponse);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                lt.error();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //FireBase
    public interface GetUserDetailsCallback {
        void onComplete(User user);

        void onFailure();
    }

    public interface GetDestinationsForUserIDCallback {
        void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList);
    }

    //Menu
    @SuppressLint("ResourceType")
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_manu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.MyTasks:
                Intent intent = new Intent(getApplicationContext(), MyTasksList.class);
                startActivity(intent);

                return true;
            case R.id.Logout:
                Log.d("TAG", "My Account");
                ModelFirebase.getInstance().cleanData();
                stopService(new Intent(getBaseContext(), MyService.class));
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /////get my location
    public class LocationTask extends AsyncTask<Void, Void, Void> {
        volatile boolean stop = false;

        @Override
        protected Void doInBackground(Void... voids) {
            while (!stop) {
                checkMyLocation();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

        }
    }


    public void startGoogleMaps(View view) {
        if (addressResponse != null && addressResponse.size() > 0) {
            int size = this.addressResponse.size();
            String str_url = "http://www.google.com/maps/dir/?api=1&origin=";
            str_url += myLocation.getLatitude() + "," + myLocation.getLongitude() + "&";
            LatLng dest = dest = new LatLng(addressResponse.get(size - 1).latitude, addressResponse.get(size - 1).longitude);
            str_url += "destination=" + dest.latitude + "," + dest.longitude;

            String str_waypoints = "";

            //there is wayPoints
            if (size >= 3) {
                //LatLng waypoints=null;
                str_waypoints = "&waypoints=";

                LatLng waypoints = new LatLng(addressResponse.get(1).latitude, addressResponse.get(1).longitude);
                str_waypoints += waypoints.latitude + "," + waypoints.longitude;

                for (int i = 2; i < size - 1; i++) {
                    waypoints = new LatLng(addressResponse.get(i).latitude, addressResponse.get(i).longitude);
                    str_waypoints += "|" + waypoints.latitude + "," + waypoints.longitude;
                }

                str_url += str_waypoints;
            }
            str_url += "&travelmode=driving";

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str_url));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.googlemaps"));
                startActivity(intent);
            }
        } else
            Toast.makeText(getApplicationContext(), "You need to planAway in order to start navigation", Toast.LENGTH_SHORT).show();


    }


}

