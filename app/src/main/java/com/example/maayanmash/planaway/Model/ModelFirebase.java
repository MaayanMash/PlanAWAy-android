package com.example.maayanmash.planaway.Model;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Button;

import com.example.maayanmash.planaway.MapsActivity;
import com.example.maayanmash.planaway.Model.entities.Destination;
import com.example.maayanmash.planaway.Model.entities.SubTask;
import com.example.maayanmash.planaway.Model.entities.Task;
import com.example.maayanmash.planaway.Model.entities.TaskRow;
import com.example.maayanmash.planaway.Model.entities.User;
import com.example.maayanmash.planaway.MyTasksList;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFirebase {
    private String uID;
    private static ModelFirebase instance = new ModelFirebase();
    public static String todayTaskID;

    public static ModelFirebase getInstance() {
        return instance;
    }

    public User getMyUserDetails(final String uID, final MapsActivity.GetUserDetailsCallback callback) {
        this.uID = uID;
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
                } else callback.onFailure();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }

        });

        return null;
    }

    public boolean DateIsToday(String date) {

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String strDate = dateFormat.format(currentDate);
        String[] holder = strDate.split("-");
        String currentTime = holder[0] + "-" + Integer.parseInt(holder[1]) + "-" + Integer.parseInt(holder[2]);
        //String currentTime = "2018-6-1";
        return date.equals(currentTime);
    }

    public String getuID() {
        return uID;
    }

    public void getMyDestinationsByID(final String uID, final MapsActivity.GetDestinationsForUserIDCallback callback) {
        this.uID = uID;
        final ArrayList<Task> tasks = new ArrayList<Task>();
        final ArrayList<Destination> destsToDay = new ArrayList<Destination>();
        final ArrayList<TaskRow> tasksRowList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("uid").equalTo(uID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final StringBuilder builder = new StringBuilder("");
                boolean enteredOnce = false;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot task : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) task.getValue();
                        if (DateIsToday((String) map.get("date"))) {
                            if (!enteredOnce) {
                                todayTaskID = task.getKey();
                                enteredOnce = true;
                                builder.append(task.getKey());
                            }
                            String mid = (String) map.get("mid");
                            String date = (String) map.get("date");
                            ArrayList<SubTask> substasks = new ArrayList<>();

                            Object dests = map.get("destinations");
                            if (dests instanceof ArrayList) {
                                ArrayList<Map> destsMap = (ArrayList<Map>) dests;
                                for (Map dest : destsMap) {
                                    String did = (String) dest.get("did");
                                    boolean isDone = (boolean) dest.get("isDone");
                                    substasks.add(new SubTask(did, isDone));
                                }

                            } else if (dests instanceof Map) {
                                Map<String, Map> destsMap = (Map<String, Map>) dests;
                                int size = destsMap.size() - 1;
                                for (int i = 0; i < size; i++) {
                                    String index = "" + i;
                                    Map<String, Object> tempMap = destsMap.get(index);
                                    String did = (String) tempMap.get("did");
                                    boolean isDone = (boolean) tempMap.get("isDone");
                                    substasks.add(new SubTask(did, isDone));
                                }
                            }
                            Task newTask = new Task(mid, uID, date, substasks);
                            tasks.add(newTask);
                        }
                    }
                }
                if (tasks.size() > 0) {
                    Log.d("TAG", ">>>>>>>>>>>>>> TASK " + tasks.get(0));

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
                                            String name = (String) map.get("name");
                                            String address = (String) map.get("address");
                                            Double latitude = (Double) map.get("latitude");
                                            Double longitude = (Double) map.get("longitude");
                                            Destination newDest = new Destination(did, mid, name, address, latitude, longitude);
                                            destsToDay.add(newDest);
                                            tasksRowList.add(new TaskRow(address, st.isDone(), did, name));

                                        }
                                    }
                                }
                            }
                            callback.onDestination(destsToDay, builder.toString(), tasksRowList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("TAG", "failed to get task");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "failed to get task");
            }
        });
    }

    public void updateMyLocation(Double latitude, Double longitude) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("users").child(uID).child("latitude").setValue(latitude);
        firebaseDatabase.getReference("users").child(uID).child("longitude").setValue(longitude);
    }

    public void updateDestinationArrivalForTask(String dID,boolean isDone) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        Query query = FirebaseDatabase.getInstance().getReference("tasks/" + todayTaskID).child("destinations");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dest : dataSnapshot.getChildren()) {
                    Map<String, Object> subTask = (Map<String, Object>) dest.getValue();

                    if (subTask.isEmpty() || subTask == null)
                        return;

                    if (((String) subTask.get("did")).equals(dID)) {
                        firebaseDatabase.getReference("tasks/" + todayTaskID).child("destinations").child("" + dest.getKey().toString()).child("isDone").setValue(isDone);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void cleanData(){
        uID = null;
        todayTaskID = null;
    }

}
