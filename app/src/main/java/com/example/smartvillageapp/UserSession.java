package com.example.smartvillageapp;

public class UserSession {

    public static String userId = "";
    public static String username = "";
    public static String role = "";

    // Optional reset
    public static void clear() {
        userId = "";
        username = "";
        role = "";
    }
}