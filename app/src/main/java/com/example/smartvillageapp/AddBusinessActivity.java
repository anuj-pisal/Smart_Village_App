package com.example.smartvillageapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddBusinessActivity extends BaseActivity {

    EditText name, domain, address, description, phone, email;
    Button saveBtn;
    FirebaseFirestore db;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        name = findViewById(R.id.et_name);
        domain = findViewById(R.id.et_domain);
        address = findViewById(R.id.et_address);
        description = findViewById(R.id.et_description);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        saveBtn = findViewById(R.id.btn_save_business);
        imagePreview = findViewById(R.id.business_image_preview);
        Button selectImageBtn = findViewById(R.id.btn_select_image);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        selectImageBtn.setOnClickListener(v -> openGallery());
        saveBtn.setOnClickListener(v -> saveBusiness());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    private byte[] compressImage(Uri uri) throws Exception {

        Bitmap bitmap = MediaStore.Images.Media
                .getBitmap(this.getContentResolver(), uri);

        Bitmap scaled = Bitmap.createScaledBitmap(
                bitmap, 800, 800, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG,
                70, baos);   // 70% quality

        return baos.toByteArray();
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void saveBusiness() {
        saveBtn.setEnabled(false);

        String bName = name.getText().toString().trim();

        if (bName.isEmpty()) {
            name.setError(getString(R.string.required));
            saveBtn.setEnabled(true);
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this,
                    "Please select image",
                    Toast.LENGTH_SHORT).show();
            saveBtn.setEnabled(true);
            return;
        }

        try {

            byte[] compressedImage = compressImage(imageUri);

            StorageReference storageRef =
                    storage.getReference()
                            .child("business_images/" +
                                    System.currentTimeMillis() + ".jpg");

            storageRef.putBytes(compressedImage)
                    .addOnSuccessListener(taskSnapshot ->
                            storageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {

                                        String imageUrl = uri.toString();

                                        Map<String, Object> businessMap =
                                                new HashMap<>();

                                        businessMap.put("name",
                                                name.getText().toString());
                                        businessMap.put("domain",
                                                domain.getText().toString());
                                        businessMap.put("address",
                                                address.getText().toString());
                                        businessMap.put("description",
                                                description.getText().toString());
                                        businessMap.put("phone",
                                                phone.getText().toString());
                                        businessMap.put("email",
                                                email.getText().toString());
                                        businessMap.put("imageUrl",
                                                imageUrl);
                                        businessMap.put("createdAt",
                                                FieldValue.serverTimestamp());

                                        FirebaseFirestore.getInstance()
                                                .collection("businesses")
                                                .add(businessMap)
                                                .addOnSuccessListener(doc -> {
                                                    AppLogger.log(
                                                            "Business Added (by user)",
                                                             UserSession.username + " (id:" + UserSession.userId + ")",
                                                            "user",
                                                            "Business: Used added business - (" + name.getText().toString() +")" +
                                                                    " Email : " + email.getText().toString()
                                                    );
                                                    Toast.makeText(this,
                                                            "Business Added",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                });
                                    }))
                    .addOnFailureListener(e -> {
                            Toast.makeText(this,
                                    "Upload Failed",
                                    Toast.LENGTH_SHORT).show();
                            saveBtn.setEnabled(true);
                    });

        } catch (Exception e) {
            e.printStackTrace();
            saveBtn.setEnabled(true);
        }
    }

}
