package com.example.maayanmash.planaway.Model;

import android.accounts.Account;
import android.app.MediaRouteActionProvider;
import android.util.Log;

import com.example.maayanmash.planaway.MapsActivity;
import com.example.maayanmash.planaway.Model.entities.SubTask;
import com.example.maayanmash.planaway.Model.entities.User;
import com.example.maayanmash.planaway.MyAddress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class ModelFirebase {

    private static ModelFirebase instance = new ModelFirebase();

    public static ModelFirebase getInstance(){
        return instance;
    }


    public User getMyUserDetails(String uID, MapsActivity.GetUserDetailsCallback callback){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String mid = (String) map.get("mid");
                String name = (String) map.get("name");
                String phone = (String) map.get("phone");
                String address = (String) map.get("address");
                String email = (String) map.get("email");
                Double latitude = (Double) map.get("latitude");
                Double longitude = (Double) map.get("longitude");
                User user = new User(uID,name,phone,address,latitude,longitude,null,mid);

                callback.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG","faled to get user");

            }
        });

        return null;
    }

    public String getMyDestinationsByID(String uID, MapsActivity.GetUserDetailsCallback callback){
        DatabaseReference userRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("uid").equalTo(uID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mid="";
                Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                mid= (String) map.get("mid");
                callback.onDestination(mid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG","faled to get user");

            }
        });

        return null;
    }
}
