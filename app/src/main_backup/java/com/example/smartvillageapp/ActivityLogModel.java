package com.example.smartvillageapp;

public class ActivityLogModel {

    public String action;     // "User Login", "Bill Added"
    public String userId;     // who performed
    public String role;       // admin / user
    public String details;    // optional info
    public long timestamp;

    public ActivityLogModel() {}
}