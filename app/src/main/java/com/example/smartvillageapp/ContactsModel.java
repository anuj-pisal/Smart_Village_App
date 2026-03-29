package com.example.smartvillageapp;

public class ContactsModel {

    private String name;
    private String designation;
    private String phone;
    private String email;

    public ContactsModel() {
        // Required for Firestore
    }

    public ContactsModel(String name, String designation, String phone, String email) {
        this.name = name;
        this.designation = designation;
        this.phone = phone;
        this.email = email;
    }

    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}
