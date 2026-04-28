package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddJobActivity extends BaseActivity {

    EditText title, req, desc;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        title = findViewById(R.id.job_title);
        req = findViewById(R.id.job_requirement);
        desc = findViewById(R.id.job_desc);
        submit = findViewById(R.id.submit_job);

        submit.setOnClickListener(v -> {
            submit.setEnabled(false);
            addJob();
        });
    }

    private void addJob() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();
        map.put("title", title.getText().toString());
        map.put("requirement", req.getText().toString());
        map.put("description", desc.getText().toString());
        map.put("postedBy", userId);
        map.put("status", "open");

        FirebaseFirestore.getInstance()
                .collection("jobs")
                .add(map)
                .addOnSuccessListener(unused -> {
                    AppLogger.log(
                            "Job Posted",
                            UserSession.username + " (id:" + UserSession.userId + ")",
                            "user",
                            "Job: posted job entitled (" + title.getText().toString() + ")"
                    );
                    Toast.makeText(this, "Job Posted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post job", Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                });

    }
}