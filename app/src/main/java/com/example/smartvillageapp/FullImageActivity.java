package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    PhotoView image;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_full_image);

        image = findViewById(R.id.full_image);

        String url = getIntent().getStringExtra("url");

        Glide.with(this)
                .load(url)
                .into(image);
    }
}