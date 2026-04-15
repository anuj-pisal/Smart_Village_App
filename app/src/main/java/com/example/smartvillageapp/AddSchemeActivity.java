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

public class AddSchemeActivity extends AppCompatActivity {

    ImageView image;
    EditText title, shortDesc, fullDesc, benefits, eligibility, documents, applyLink;
    Button selectImg, uploadBtn;

    Uri imageUri;

    FirebaseStorage storage;
    FirebaseFirestore db;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_scheme);

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        shortDesc = findViewById(R.id.shortDesc);
        fullDesc = findViewById(R.id.fullDesc);
        benefits = findViewById(R.id.benefits);
        eligibility = findViewById(R.id.eligibility);
        documents = findViewById(R.id.documents);
        applyLink = findViewById(R.id.applyLink);
        selectImg = findViewById(R.id.select_img);
        uploadBtn = findViewById(R.id.upload_btn);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        selectImg.setOnClickListener(v -> openGallery());
        uploadBtn.setOnClickListener(v -> uploadScheme());
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

    private void uploadScheme() {

        if (imageUri == null) {
            Toast.makeText(this, "Select image", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "scheme_" + System.currentTimeMillis();

        StorageReference ref = storage.getReference()
                .child("schemes_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            Map<String, Object> map = new HashMap<>();

                            map.put("title", title.getText().toString());
                            map.put("shortDesc", shortDesc.getText().toString());
                            map.put("fullDesc", fullDesc.getText().toString());
                            map.put("benefits", benefits.getText().toString());
                            map.put("eligibility", eligibility.getText().toString());
                            map.put("documents", documents.getText().toString());
                            map.put("applyLink", applyLink.getText().toString());
                            map.put("image", uri.toString());

                            db.collection("schemes")
                                    .add(map)
                                    .addOnSuccessListener(d -> {
                                        AppLogger.log(
                                                "Scheme Added",
                                                "NA",
                                                "admin",
                                                "Scheme: ("+title.getText().toString()+") is added"
                                        );
                                        Toast.makeText(this, "Scheme Added!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });

                        }));
    }
}