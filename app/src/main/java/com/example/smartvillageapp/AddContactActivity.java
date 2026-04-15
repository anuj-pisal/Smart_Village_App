package com.example.smartvillageapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {

    EditText name, designation, phone, email, priority;
    Button addBtn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_contact);

        name = findViewById(R.id.name);
        designation = findViewById(R.id.designation);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        priority = findViewById(R.id.priority);
        addBtn = findViewById(R.id.add_btn);

        db = FirebaseFirestore.getInstance();

        addBtn.setOnClickListener(v -> addContact());
    }

    private void addContact() {

        String n = name.getText().toString().trim();
        String d = designation.getText().toString().trim();
        String p = phone.getText().toString().trim();
        String e = email.getText().toString().trim();
        String pr = priority.getText().toString().trim();

        // 🔥 VALIDATION
        if (TextUtils.isEmpty(n) || TextUtils.isEmpty(d) ||
                TextUtils.isEmpty(p) || TextUtils.isEmpty(e) || TextUtils.isEmpty(pr)) {

            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        int priorityValue = Integer.parseInt(pr);

        // 🔥 DATA MAP
        Map<String, Object> map = new HashMap<>();
        map.put("name", n);
        map.put("designation", d);
        map.put("phone", p);
        map.put("email", e);
        map.put("priority", priorityValue);

        // 🔥 FIRESTORE INSERT
        db.collection("contacts")
                .add(map)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Contact Added!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e1 -> {
                    Toast.makeText(this, "Error: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}