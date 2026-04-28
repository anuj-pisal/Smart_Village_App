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

public class AddNoticeActivity extends BaseActivity {

    ImageView imageView;
    EditText title;
    Button selectBtn, uploadBtn;

    Uri imageUri;

    FirebaseStorage storage;
    FirebaseFirestore db;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_notice);

        imageView = findViewById(R.id.notice_image);
        title = findViewById(R.id.title);
        selectBtn = findViewById(R.id.select_image_btn);
        uploadBtn = findViewById(R.id.upload_btn);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        selectBtn.setOnClickListener(v -> openGallery());

        uploadBtn.setOnClickListener(v -> {
            uploadBtn.setEnabled(false);
            uploadNotice();
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
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadNotice() {

        String t = title.getText().toString().trim();

        if (t.isEmpty() || imageUri == null) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        }

        String fileName = "notice_" + System.currentTimeMillis();

        StorageReference ref = storage.getReference()
                .child("notice_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            String imageUrl = uri.toString();

                            Map<String, Object> map = new HashMap<>();
                            map.put("title", t);
                            map.put("imageUrl", imageUrl);
                            map.put("timestamp", System.currentTimeMillis());

                            db.collection("notices")
                                    .add(map)
                                    .addOnSuccessListener(d -> {
                                        AppLogger.log(
                                                "Notice Published",
                                                "NA",
                                                "admin",
                                                "Notice: ( "+ t +" ) is added"
                                        );
                                        Toast.makeText(this, getString(R.string.notice_published), Toast.LENGTH_SHORT).show();
                                        finish();
                                    });

                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.upload_failed_2), Toast.LENGTH_SHORT).show();
                    uploadBtn.setEnabled(true);
                });
    }
}