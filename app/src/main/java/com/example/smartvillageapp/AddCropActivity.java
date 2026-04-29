package com.example.smartvillageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddCropActivity extends BaseActivity {

    ImageView image;
    EditText title, desc, cropLink;
    Button selectImg, addBtn;

    Uri imageUri;

    FirebaseStorage storage;
    FirebaseFirestore db;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_crop_admin); // Reusing the same layout

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        cropLink = findViewById(R.id.crop_link);
        selectImg = findViewById(R.id.select_img);
        addBtn = findViewById(R.id.add_btn);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        selectImg.setOnClickListener(x -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, PICK_IMAGE);
        });

        addBtn.setOnClickListener(x -> {
            addBtn.setEnabled(false);
            uploadCrop();
        });
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_IMAGE && res == RESULT_OK && data != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private void uploadCrop() {
        String linkText = cropLink.getText().toString().trim();

        if (title.getText().toString().trim().isEmpty() || desc.getText().toString().trim().isEmpty() || linkText.isEmpty() || imageUri == null) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            addBtn.setEnabled(true);
            return;
        }

        if (!android.util.Patterns.WEB_URL.matcher(linkText).matches()) {
            Toast.makeText(this, getString(R.string.invalid_link_format), Toast.LENGTH_SHORT).show();
            addBtn.setEnabled(true);
            return;
        }

        String fileName = "crop_" + System.currentTimeMillis();
        StorageReference ref = storage.getReference().child("agriculture/crop_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("title", title.getText().toString());
                            map.put("description", desc.getText().toString());
                            map.put("imageUrl", uri.toString());
                            map.put("cropLink", linkText);

                            db.collection("agriculture_crops").add(map)
                                    .addOnSuccessListener(documentReference -> {
                                        AppLogger.log(
                                                "Crop Info. Added",
                                                "NA",
                                                "admin",
                                                "Crop: (" + title.getText().toString() + ") info. is added"
                                        );
                                        Toast.makeText(AddCropActivity.this, getString(R.string.crop_added), Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                    addBtn.setEnabled(true);
                });
    }
}
