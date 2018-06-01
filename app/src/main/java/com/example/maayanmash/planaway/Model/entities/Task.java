package com.example.maayanmash.planaway.Model.entities;

import java.util.List;

public class Task {
    private String mID;
    private String uID;
    private String date;
    private List<SubTask> dests;

    public Task(String mID, String uID, String date, List<SubTask> dests) {
        this.mID = mID;
        this.uID = uID;
        this.date = date;
        this.dests = dests;
    }

    public String getmID() {

        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
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

    @Override
    public String toString() {
        return "Task{" +
                "mID='" + mID + '\'' +
                ", uID='" + uID + '\'' +
                ", date='" + date + '\'' +
                ", dests=" + dests.toString() +
                '}';
    }
}
