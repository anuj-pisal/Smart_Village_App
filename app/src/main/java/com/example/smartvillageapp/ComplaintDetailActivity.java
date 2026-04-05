package com.example.smartvillageapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ComplaintDetailActivity extends AppCompatActivity {

    ImageView img;
    TextView desc, status;
    LinearLayout proofContainer;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_complaint_detail);

        img = findViewById(R.id.img);
        desc = findViewById(R.id.desc);
        status = findViewById(R.id.status);
        proofContainer = findViewById(R.id.proof_container);

        String id = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance()
                .collection("complaints")
                .document(id)
                .get()
                .addOnSuccessListener(d -> {

                    desc.setText(d.getString("description"));
                    status.setText("Status : " + d.getString("status"));

                    if ("pending".equals(d.getString("status")))
                        status.setTextColor(Color.YELLOW);
                    else if ("in_progress".equals(d.getString("status")))
                        status.setTextColor(Color.parseColor("#00ccff"));
                    else
                        status.setTextColor(Color.GREEN);

                    Glide.with(this)
                            .load(d.getString("imageUrl"))
                            .into(img);

                    List<String> proofs = (List<String>) d.get("proofImages");

                    if (proofs != null && !proofs.isEmpty()) {

                        for (String url : proofs) {

                            ImageView imgView = new ImageView(this);
                            imgView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    400
                            ));
                            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imgView.setPadding(0, 10, 0, 10);

                            Glide.with(this).load(url).into(imgView);

                            proofContainer.addView(imgView);
                        }
                    }
                });
    }
}