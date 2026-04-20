package com.example.smartvillageapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddBillActivity extends BaseActivity {

    EditText title, amount, desc, date;
    Button selectImages, addBtn;

    List<Uri> imageUris = new ArrayList<>();

    FirebaseFirestore db;
    FirebaseStorage storage;

    String userId;
    TextView imageCount;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_bill);
        imageCount = findViewById(R.id.image_count);
        title = findViewById(R.id.title);
        amount = findViewById(R.id.amount);
        desc = findViewById(R.id.desc);
        date = findViewById(R.id.date);
        selectImages = findViewById(R.id.select_images);
        addBtn = findViewById(R.id.add_btn);

        userId = getIntent().getStringExtra("userId");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        selectImages.setOnClickListener(v -> openGallery());
        addBtn.setOnClickListener(v -> uploadBill());
        date.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog dp = new DatePickerDialog(this,
                    (view, year, month, day) -> {

                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day);

                        SimpleDateFormat format =
                                new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

                        date.setText(format.format(selected.getTime()));

                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );

            dp.show();
        });
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

            imageUris.clear(); // 🔥 IMPORTANT (avoid duplicates)

            if (data.getClipData() != null) {

                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    imageUris.add(data.getClipData().getItemAt(i).getUri());
                }

            } else {
                imageUris.add(data.getData());
            }

            // 🔥 UPDATE UI HERE
            imageCount.setText(imageUris.size() + " images selected");
        }
    }

    private void uploadBill() {

        // 🔥 STEP 1: FETCH USERNAME FIRST
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    String username;

                    if (doc.exists()) {
                        username = doc.getString("username");
                    } else {
                        username = "";
                    }

                    // 🔥 STEP 2: UPLOAD IMAGES
                    List<String> urls = new ArrayList<>();

                    for (Uri uri : imageUris) {

                        String name = "bill_" + System.currentTimeMillis();

                        StorageReference ref = storage.getReference()
                                .child("bills/" + name);

                        ref.putFile(uri)
                                .continueWithTask(t -> ref.getDownloadUrl())
                                .addOnSuccessListener(u -> {

                                    urls.add(u.toString());

                                    if (urls.size() == imageUris.size()) {

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("title", title.getText().toString());
                                        map.put("amount", Long.parseLong(amount.getText().toString()));
                                        map.put("description", desc.getText().toString());
                                        map.put("dueDate", date.getText().toString());
                                        map.put("status", "unpaid");
                                        map.put("userId", userId);
                                        map.put("images", urls);
                                        map.put("timestamp", System.currentTimeMillis());

                                        db.collection("bills").add(map);

                                        Toast.makeText(this, getString(R.string.bill_added), Toast.LENGTH_SHORT).show();

                                        // 🔥 STEP 3: LOG CORRECTLY
                                        AppLogger.log(
                                                "Bill Added",
                                                username + " (id : " + userId +" )",
                                                "admin",
                                                "Bill: (" + title.getText().toString()
                                                        + ") of Rs. " + amount.getText().toString()
                                                        + " added for user: " + username
                                        );

                                        finish();
                                    }
                                });
                    }
                });
    }
}