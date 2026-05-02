package com.example.smartvillageapp;

public class NoticeModel {

    public String title;
    public String imageUrl;
    public String type; // "image" or "text"
    public String description;
    public long timestamp;
    public long expiryTimestamp;

    public NoticeModel() {}
}