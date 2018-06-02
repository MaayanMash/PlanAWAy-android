package com.example.maayanmash.planaway.Model.entities;

public class TaskRow {
    private String address;
    private boolean isDone;
    private String did;
    private String name;


    public TaskRow(){

    }
    public TaskRow(String address, boolean isDone, String did, String name) {
        this.address = address;
        this.isDone = isDone;
        this.did = did;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TaskRow{" +
                "address='" + address + '\'' +
                ", isDone=" + isDone +
                ", did='" + did + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
