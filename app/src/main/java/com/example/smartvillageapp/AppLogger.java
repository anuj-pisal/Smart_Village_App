package com.example.smartvillageapp;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppLogger {

    public static void log(String action, String userId, String role, String details) {

        Map<String, Object> log = new HashMap<>();
        log.put("action", action);
        log.put("userId", userId);
        log.put("role", role);
        log.put("details", details);
        log.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("logs")
                .add(log);
    }
}