package com.example.smartvillageapp;

import java.util.List;

public class ComplaintModel {
    public String id;
    public String userId;
    public String description;
    public String imageUrl;
    public String status;
    public long timestamp;

    public List<String> proofImages;

    public ComplaintModel() {}
}