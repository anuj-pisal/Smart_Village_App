package com.example.smartvillageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminComplaintDetailActivity extends AppCompatActivity {

    Spinner statusSpinner;
    Button uploadBtn, updateBtn;
    TextView imageCount;

    List<Uri> imageUris = new ArrayList<>();
    List<String> urls = new ArrayList<>();

    String docId, userId, username;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_complaint_detail);

        statusSpinner = findViewById(R.id.status_spinner);
        uploadBtn = findViewById(R.id.upload_btn);
        updateBtn = findViewById(R.id.update_btn);
        imageCount = findViewById(R.id.image_count);

        docId = getIntent().getStringExtra("docId");
        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");


        String[] statuses = {"pending", "in_progress", "resolved"};
        statusSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statuses));

        uploadBtn.setOnClickListener(v -> openGallery());
        updateBtn.setOnClickListener(v -> updateComplaint());
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (req == 1 && res == RESULT_OK && data != null) {

            imageUris.clear();

            if (data.getClipData() != null) {

                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    imageUris.add(data.getClipData().getItemAt(i).getUri());
                }

            } else {
                imageUris.add(data.getData());
            }

            imageCount.setText(imageUris.size() + " images selected");
        }
    }

    private void updateComplaint() {

        // 🔥 CASE 1: NO IMAGES
        if (imageUris.isEmpty()) {

            Map<String, Object> map = new HashMap<>();
            map.put("status", statusSpinner.getSelectedItem().toString());

            FirebaseFirestore.getInstance()
                    .collection("complaints")
                    .document(docId)
                    .update(map)
                    .addOnSuccessListener(unused -> {
                        AppLogger.log(
                                "Complaint Updated",
                                username + "(id: " + userId + ")",
                                "admin",
                                "Complaint: the status is updated to ( " + statusSpinner.getSelectedItem().toString()
                                        +" )"
                        );
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", "Error: " + e.getMessage());
                    });

            return;
        }

        // 🔥 CASE 2: WITH IMAGES
        FirebaseStorage storage = FirebaseStorage.getInstance();
        urls.clear();

        for (Uri uri : imageUris) {

            StorageReference ref = storage.getReference()
                    .child("complaint_resolution_images/" + System.currentTimeMillis());

            ref.putFile(uri)
                    .continueWithTask(t -> ref.getDownloadUrl())
                    .addOnSuccessListener(u -> {

                        urls.add(u.toString());

                        if (urls.size() == imageUris.size()) {

                            Map<String, Object> map = new HashMap<>();
                            map.put("status", statusSpinner.getSelectedItem().toString());
                            map.put("proofImages", urls);

                            FirebaseFirestore.getInstance()
                                    .collection("complaints")
                                    .document(docId)
                                    .update(map)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d("error", "Error: " + e.getMessage());
                                    });
                        }
                    });
        }
    }
}