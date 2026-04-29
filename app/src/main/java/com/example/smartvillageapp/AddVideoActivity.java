package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddVideoActivity extends BaseActivity {

    EditText title, url;
    Button addBtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video_admin); // Reusing the same layout

        title = findViewById(R.id.title);
        url = findViewById(R.id.url);
        addBtn = findViewById(R.id.add_btn);

        db = FirebaseFirestore.getInstance();

        addBtn.setOnClickListener(v -> {
            addBtn.setEnabled(false);
            uploadVideo();
        });
    }

    private void uploadVideo() {
        String titleStr = title.getText().toString().trim();
        String urlStr = url.getText().toString().trim();

        if (titleStr.isEmpty() || urlStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            addBtn.setEnabled(true);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("title", titleStr);
        map.put("videoUrl", urlStr);

        db.collection("agriculture_videos").add(map)
                .addOnSuccessListener(documentReference -> {
                    AppLogger.log(
                            "Video Added",
                            "NA",
                            "admin",
                            "Video: (" + titleStr + ") is added"
                    );
                    Toast.makeText(AddVideoActivity.this, getString(R.string.video_added), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.error_prefix) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    addBtn.setEnabled(true);
                });
    }
}
