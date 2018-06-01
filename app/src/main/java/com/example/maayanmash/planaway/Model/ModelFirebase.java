package com.example.maayanmash.planaway.Model;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.maayanmash.planaway.MapsActivity;
import com.example.maayanmash.planaway.Model.entities.Destination;
import com.example.maayanmash.planaway.Model.entities.SubTask;
import com.example.maayanmash.planaway.Model.entities.Task;
import com.example.maayanmash.planaway.Model.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ModelFirebase {

    private static ModelFirebase instance = new ModelFirebase();

    public static ModelFirebase getInstance() {
        return instance;
    }


    public User getMyUserDetails(final String uID, final MapsActivity.GetUserDetailsCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String mid = (String) map.get("mid");
                String name = (String) map.get("name");
                String phone = (String) map.get("phone");
                String address = (String) map.get("address");
                String email = (String) map.get("email");
                Double latitude = (Double) map.get("latitude");
                Double longitude = (Double) map.get("longitude");
                User user = new User(uID, name, phone, address, latitude, longitude, null, mid);
                callback.onComplete(user);
            }
            else Log.d("TAG","else");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("TAG", "failed to get user");

        }
    });

        return null;
}

    public boolean DateIsToday(String date) {

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String strDate = dateFormat.format(currentDate);
        //String strDate="2018-5-30";
        String[] holder = strDate.split("-");
        String currentTime = holder[0] + "-" + Integer.parseInt(holder[1]) + "-" + Integer.parseInt(holder[2]);

        return date.equals(currentTime);
    }

    public void getMyDestinationsByID(final String uID, final MapsActivity.GetUserDetailsCallback callback) {
        final ArrayList<Task> tasks = new ArrayList<Task>();
        final ArrayList<Destination> destsToDay = new ArrayList<Destination>();

        Query query = FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("uid").equalTo(uID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot task : dataSnapshot.getChildren()) {
                        Log.d("TAG",task.toString());
                        Map<String, Object> map = (Map<String, Object>) task.getValue();
                        if (DateIsToday((String) map.get("date"))) {
                            String mid = (String) map.get("mid");
                            String date = (String) map.get("date");
                            ArrayList<Map> destsMap = (ArrayList<Map>) map.get("destinations");
                            ArrayList<SubTask> substasks = new ArrayList<>();
                            for (Map dest : destsMap) {
                                String did = (String) dest.get("did");
                                boolean isDone = (boolean) dest.get("isDone");
                                SubTask subtask = new SubTask(did, isDone);
                                substasks.add(subtask);
                            }
                            Task newTask = new Task(mid, uID, date, substasks);
                            tasks.add(newTask);
                            Log.d("TAG",newTask.toString());
                        }
                    }
                }
                if(tasks.size()>0){
                    final Task myTask = tasks.get(0);
                    Query query = FirebaseDatabase.getInstance().getReference().child("destinations").orderByChild("mid").equalTo(myTask.getmID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dest : dataSnapshot.getChildren()) {
                                    for (SubTask st : myTask.getDests()) {
                                        if (dest.getKey().equals(st.getdID())) {
                                            Map<String, Object> map = (Map<String, Object>) dest.getValue();
                                            String did = dest.getKey();
                                            String mid = (String) map.get("mid");
                                            String name= (String) map.get("name");
                                            String address = (String) map.get("address");
                                            Double latitude = (Double) map.get("latitude");
                                            Double longitude = (Double) map.get("longitude");
                                            Destination newDest = new Destination(did, mid,name, address, latitude, longitude);
                                            destsToDay.add(newDest);
                                        }
                                    }
                                }
                                Log.d("TAG", "destsToDay= " + destsToDay);
                                callback.onDestination(destsToDay);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { Log.d("TAG", "failed to get destination"); }
                    });
                }

                callback.onDestination(destsToDay);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "failed to get task");

            }
        });


    }

    public void updateMyLocation(String uID, Double latitude, Double longitude){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("users").child(uID).child("latitude").setValue(latitude);
        firebaseDatabase.getReference("users").child(uID).child("longitude").setValue(longitude);
    }
}
