package com.example.smartvillageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.*;

public class AddDevelopmentActivity extends AppCompatActivity {

    EditText title, shortDesc, fullDesc, budget, location, progress, startDate;
    Spinner status;
    Button selectImages, uploadBtn;
    TextView imageCount;

    List<Uri> imageUris = new ArrayList<>();

    FirebaseStorage storage;
    FirebaseFirestore db;

    private static final int PICK_IMAGES = 101;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_development);

        title = findViewById(R.id.title);
        shortDesc = findViewById(R.id.shortDesc);
        fullDesc = findViewById(R.id.fullDesc);
        budget = findViewById(R.id.budget);
        location = findViewById(R.id.location);
        progress = findViewById(R.id.progress);
        startDate = findViewById(R.id.startDate);
        status = findViewById(R.id.status);
        selectImages = findViewById(R.id.select_images);
        uploadBtn = findViewById(R.id.upload_btn);
        imageCount = findViewById(R.id.image_count);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔥 STATUS DROPDOWN
        String[] statuses = {"Ongoing", "Completed"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statuses);
        status.setAdapter(adapter);

        selectImages.setOnClickListener(v -> openGallery());
        uploadBtn.setOnClickListener(v -> uploadDevelopment());
        startDate.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> {

                    Calendar selected = Calendar.getInstance();
                    selected.set(y, m, d);

                    // 🔥 FORMAT: 1 March 2026
                    SimpleDateFormat format =
                            new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

                    String formattedDate = format.format(selected.getTime());

                    startDate.setText(formattedDate);

                }, year, month, day);

        dialog.show();
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_IMAGES && res == RESULT_OK && data != null) {

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

    private void uploadDevelopment() {

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Select images", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> imageUrls = new ArrayList<>();

        for (Uri uri : imageUris) {

            String fileName = "dev_" + System.currentTimeMillis();

            StorageReference ref = storage.getReference()
                    .child("development_images/" + fileName);

            ref.putFile(uri)
                    .continueWithTask(task -> ref.getDownloadUrl())
                    .addOnSuccessListener(downloadUri -> {

                        imageUrls.add(downloadUri.toString());

                        // 🔥 WHEN ALL IMAGES UPLOADED
                        if (imageUrls.size() == imageUris.size()) {
                            saveToFirestore(imageUrls);
                        }
                    });
        }
    }

    private void saveToFirestore(List<String> images) {

        Map<String, Object> map = new HashMap<>();

        map.put("title", title.getText().toString());
        map.put("shortDesc", shortDesc.getText().toString());
        map.put("fullDesc", fullDesc.getText().toString());
        map.put("budget", budget.getText().toString());
        map.put("location", location.getText().toString());
        map.put("progress", Integer.parseInt(progress.getText().toString()));
        map.put("startDate", startDate.getText().toString());
        map.put("status", status.getSelectedItem().toString());
        map.put("images", images);

        db.collection("developments")
                .add(map)
                .addOnSuccessListener(d -> {
                    Toast.makeText(this, "Development Added!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}