package com.example.smartvillageapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.*;

public class AddComplaintActivity extends BaseActivity {

    ImageView preview;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_complaint);

        preview = findViewById(R.id.preview);
        Button camera = findViewById(R.id.camera_btn);
        Button gallery = findViewById(R.id.gallery_btn);
        Button submit = findViewById(R.id.submit_btn);
        EditText desc = findViewById(R.id.desc);

        camera.setOnClickListener(v -> {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, 1);
        });

        preview.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, 2);
        });

        gallery.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, 2);
        });

        submit.setOnClickListener(v -> {

            if (imageUri == null) {
                Toast.makeText(this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                return;
            }

            String text = desc.getText().toString();
            submit.setEnabled(false);

            FirebaseStorage.getInstance()
                    .getReference("complaints/" + System.currentTimeMillis())
                    .putFile(imageUri)
                    .addOnSuccessListener(t -> t.getStorage().getDownloadUrl()
                            .addOnSuccessListener(url -> {

                                Map<String, Object> data = new HashMap<>();
                                data.put("userId", FirebaseAuth.getInstance().getUid());
                                data.put("description", text);
                                data.put("imageUrl", url.toString());
                                data.put("status", "pending");
                                data.put("timestamp", System.currentTimeMillis());
                                data.put("proofImages", new ArrayList<>());

                                FirebaseFirestore.getInstance()
                                        .collection("complaints")
                                        .add(data);

                                AppLogger.log(
                                        "Complaint Added",
                                        UserSession.username + " (id:" + UserSession.userId + ")",
                                        "user",
                                        "Complaint: title - (" + text +")"
                                );

                                Toast.makeText(this, getString(R.string.complaint_registered), Toast.LENGTH_SHORT).show();

                                finish();
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                        submit.setEnabled(true);
                    });

        });
    }

    @Override
    protected void onActivityResult(int r, int c, Intent d) {
        super.onActivityResult(r, c, d);

        if (c == RESULT_OK) {

            if (r == 1) {
                // CAMERA
                Bundle extras = d.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Convert bitmap to URI
                imageUri = getImageUri(imageBitmap);

                preview.setImageBitmap(imageBitmap);

            } else if (r == 2) {
                // GALLERY
                imageUri = d.getData();
                preview.setImageURI(imageUri);
            }
        }
    }

    public Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "temp",
                null
        );
        return Uri.parse(path);
    }
}