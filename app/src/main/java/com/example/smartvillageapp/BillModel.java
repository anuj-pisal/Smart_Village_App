package com.example.smartvillageapp;

import java.util.List;

public class BillModel {
    public String id;
    public String userId;
    public String title;
    public String description;
    public String dueDate;
    public String status;
    public long amount;

    public List<String> images;

    public BillModel() {}
}
