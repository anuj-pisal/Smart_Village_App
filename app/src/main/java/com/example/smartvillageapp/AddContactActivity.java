package com.example.smartvillageapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends BaseActivity {

    EditText name, designation, phone, email, priority;
    Button addBtn;

    FirebaseFirestore db;
    String contactId = null;

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

        if (getIntent().hasExtra("id")) {
            contactId = getIntent().getStringExtra("id");
            name.setText(getIntent().getStringExtra("name"));
            designation.setText(getIntent().getStringExtra("designation"));
            phone.setText(getIntent().getStringExtra("phone"));
            email.setText(getIntent().getStringExtra("email"));
            addBtn.setText("Update Contact");
        }

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

            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
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

        // 🔥 FIRESTORE INSERT OR UPDATE
        if (contactId == null) {
            db.collection("contacts")
                    .add(map)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, getString(R.string.contact_added), Toast.LENGTH_SHORT).show();
                        AppLogger.log(
                                "Contact Added",
                                "NA",
                                "admin",
                                "Contact: " + p
                                        + " of ( " + n + " - " + d + " ) is added"
                        );
                        finish();
                    })
                    .addOnFailureListener(e1 -> {
                        Toast.makeText(this, getString(R.string.error_prefix) + e1.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("contacts").document(contactId)
                    .update(map)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e1 -> {
                        Toast.makeText(this, getString(R.string.error_prefix) + e1.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}