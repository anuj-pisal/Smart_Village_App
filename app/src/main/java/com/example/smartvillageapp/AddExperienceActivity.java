package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddExperienceActivity extends AppCompatActivity {

    EditText title, desc;
    Button postBtn;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_experience);

        title = findViewById(R.id.title_input);
        desc = findViewById(R.id.desc_input);
        postBtn = findViewById(R.id.post_btn);

        postBtn.setOnClickListener(v -> {

            String t = title.getText().toString();
            String d = desc.getText().toString();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(userDoc -> {

                        String name = userDoc.getString("username");

                        Map<String, Object> exp = new HashMap<>();
                        exp.put("title", t);
                        exp.put("description", d);
                        exp.put("userId", userId);
                        exp.put("userName", name);
                        exp.put("timestamp", System.currentTimeMillis());

                        db.collection("experiences").add(exp);

                        Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        });
    }
}