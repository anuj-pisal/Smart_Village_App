package com.example.smartvillageapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class NoticeDetailActivity extends BaseActivity {

    PhotoView imageView;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_notice_detail);

        imageView = findViewById(R.id.full_image);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
}