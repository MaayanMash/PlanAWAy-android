package com.example.maayanmash.planaway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.maayanmash.planaway.Model.entities.AddressHolder;

public class DefineDestinationDialogFragment extends DialogFragment {
    private AddressHolder destination=null;
    private double lat;
    private double lon;
    private String marker_name;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would You Like "+marker_name+" to Be Your Final Destination?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        destination=new AddressHolder(lat,lon);
                        Log.d("TAG","yes");
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("TAG","no");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setLat(double lat){this.lat=lat;}

    public void setLon(double lon){this.lon=lon;}

    public AddressHolder getDestination(){return this.destination;}

    public void setMarker_name(String marker_name){this.marker_name=marker_name;}

}
