package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

public class DevelopmentDetailActivity extends BaseActivity {

    Handler sliderHandler = new Handler(Looper.getMainLooper());
    Runnable sliderRunnable;
    TextView title, status, full, location, date, budget, progressText;
    ProgressBar progressBar;
    ViewPager2 slider;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_development_detail);

        title = findViewById(R.id.dev_title);
        status = findViewById(R.id.dev_status);
        full = findViewById(R.id.dev_full);
        location = findViewById(R.id.dev_location);
        date = findViewById(R.id.dev_date);
        budget = findViewById(R.id.dev_budget);
        progressText = findViewById(R.id.dev_progress_text);
        progressBar = findViewById(R.id.dev_progress);
        slider = findViewById(R.id.dev_slider);

        String id = getIntent().getStringExtra("devId");

        FirebaseFirestore.getInstance()
                .collection("developments")
                .document(id)
                .get()
                .addOnSuccessListener(d -> {

                    if (d.exists()) {

                        title.setText(d.getString("title"));
                        status.setText(d.getString("status"));
                        full.setText(d.getString("fullDesc"));

                        location.setText(d.getString("location"));
                        date.setText(d.getString("startDate"));
                        budget.setText(d.getString("budget"));

                        // 🔥 PROGRESS
                        Long prog = d.getLong("progress");
                        if (prog != null) {
                            int p = prog.intValue();
                            progressBar.setProgress(p);
                            progressText.setText(getString(R.string.progress) + p + "%");
                        }

                        // 🔥 IMAGES
                        List<String> images = (List<String>) d.get("images");
                        if (images != null && !images.isEmpty()) {
                            slider.setAdapter(new DevImageAdapter(this, images));

                            // 🔥 AUTO SCROLL SETUP
                            sliderRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    int current = slider.getCurrentItem();
                                    int next = current + 1;

                                    if (next >= images.size()) {
                                        next = 0; // loop back
                                    }

                                    slider.setCurrentItem(next, true);
                                    sliderHandler.postDelayed(this, 3000); // 3 sec
                                }
                            };

                            sliderHandler.postDelayed(sliderRunnable, 3000);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }
}