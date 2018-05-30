package com.example.maayanmash.planaway.Model.entities;

import java.util.Date;
import java.util.List;

public class Task {
    private String mID;
    private String dID;
    private String date;
    private List<SubTask> dests;

    public Task(String mID, String dID, String date, List<SubTask> dests) {
        this.mID = mID;
        this.dID = dID;
        this.date = date;
        this.dests = dests;
    }

    public String getmID() {

        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<SubTask> getDests() {
        return dests;
    }

    public void setDests(List<SubTask> dests) {
        this.dests = dests;
    }
}
