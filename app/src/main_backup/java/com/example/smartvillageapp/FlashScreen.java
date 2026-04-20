package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class FlashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);

        ImageView appLogo = findViewById(R.id.imageView2);

        Glide.with(this)
                .load(R.drawable.app_logo_3)
                .circleCrop()
                .into(appLogo);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}