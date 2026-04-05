package com.example.smartvillageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class SchemeDetailActivity extends AppCompatActivity {

    ImageView img;
    TextView title, full, benefits, eligibility, documents;
    Button applyBtn;

    String link = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_scheme_detail);

        img = findViewById(R.id.img);
        title = findViewById(R.id.title);
        full = findViewById(R.id.full);
        benefits = findViewById(R.id.benefits);
        eligibility = findViewById(R.id.eligibility);
        documents = findViewById(R.id.documents);
        applyBtn = findViewById(R.id.apply_btn);

        String id = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance()
                .collection("schemes")
                .document(id)
                .get()
                .addOnSuccessListener(d -> {

                    title.setText(d.getString("title"));
                    full.setText(d.getString("fullDesc"));
                    benefits.setText("Benefits:\n" + d.getString("benefits"));
                    eligibility.setText("Eligibility:\n" + d.getString("eligibility"));
                    documents.setText("Documents:\n" + d.getString("documents"));

                    link = d.getString("applyLink");

                    Glide.with(this).load(d.getString("image")).into(img);
                });

        applyBtn.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(i);
        });
    }
}