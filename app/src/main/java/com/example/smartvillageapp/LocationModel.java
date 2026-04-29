package com.example.smartvillageapp;
public class LocationModel {
    public String id, name, description, imageUrl;
    public double latitude, longitude;

    public LocationModel() {}

    public LocationModel(String name, String description,
                         double latitude, double longitude, String imageUrl) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
    }
}
