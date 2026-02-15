package com.example.smartvillageapp;

public class DashboardItem {
    int icon;
    String title;

    public DashboardItem(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() { return icon; }
    public String getTitle() { return title; }
}

