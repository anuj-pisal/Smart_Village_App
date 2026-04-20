package com.example.smartvillageapp;

public class BusinessModel {

    private String name;
    private String domain;
    private String address;
    private String description;
    private String phone;
    private String email;
    private String imageUrl;

    public BusinessModel() {
        // Required empty constructor for Firestore
    }

    public BusinessModel(String name, String domain, String address,
                    String description, String phone,
                    String email, String imageUrl) {

        this.name = name;
        this.domain = domain;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getImageUrl() { return imageUrl; }
}
