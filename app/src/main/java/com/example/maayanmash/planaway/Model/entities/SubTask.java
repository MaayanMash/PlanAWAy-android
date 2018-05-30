package com.example.maayanmash.planaway.Model.entities;

public class SubTask {
    private String dID;
    private boolean isDone;

    public SubTask(String dID, boolean isDone) {
        this.dID = dID;
        this.isDone = isDone;
    }

    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
