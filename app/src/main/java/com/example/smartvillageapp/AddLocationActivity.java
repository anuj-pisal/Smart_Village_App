package com.example.smartvillageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddLocationActivity extends BaseActivity {

    ImageView image;
    EditText name, desc, latitude, longitude;
    Button selectImg, addBtn;

    Uri imageUri;

    FirebaseStorage storage;
    FirebaseFirestore db;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_location);

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        selectImg = findViewById(R.id.select_img);
        addBtn = findViewById(R.id.add_btn);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        selectImg.setOnClickListener(v -> openGallery());
        addBtn.setOnClickListener(v -> {
            addBtn.setEnabled(false);
            uploadLocation();
        });
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_IMAGE && res == RESULT_OK && data != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private void uploadLocation() {

        String n = name.getText().toString().trim();
        String d = desc.getText().toString().trim();
        String latStr = latitude.getText().toString().trim();
        String lngStr = longitude.getText().toString().trim();

        if (n.isEmpty() || d.isEmpty() || latStr.isEmpty() || lngStr.isEmpty() || imageUri == null) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            addBtn.setEnabled(true);
            return;
        }

        double lat, lng;

        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.invalid_lat_lng), Toast.LENGTH_SHORT).show();
            addBtn.setEnabled(true);
            return;
        }

        String fileName = "loc_" + System.currentTimeMillis();

        StorageReference ref = storage.getReference()
                .child("location_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            Map<String, Object> map = new HashMap<>();
                            map.put("name", n);
                            map.put("description", d);
                            map.put("imageUrl", uri.toString());
                            map.put("latitude", lat);
                            map.put("longitude", lng);

                            db.collection("locations")
                                    .add(map)
                                    .addOnSuccessListener(x -> {
                                        AppLogger.log(
                                                "Location Added",
                                                "NA",
                                                "admin",
                                                "Location: (" + n + ") is added"
                                        );
                                        Toast.makeText(this, getString(R.string.location_added), Toast.LENGTH_SHORT).show();
                                        finish();
                                    });

                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                    addBtn.setEnabled(true);
                });
    }
}