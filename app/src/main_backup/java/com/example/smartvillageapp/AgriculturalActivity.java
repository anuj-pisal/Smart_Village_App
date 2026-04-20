package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AgriculturalActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_agricultural);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        AgriPagerAdapter adapter = new AgriPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 🔥 CONNECT TAB + VIEWPAGER
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {

                    if (position == 0) tab.setText("Crops");
                    else if (position == 1) tab.setText("Queries");
                    else tab.setText("Videos");

                }).attach();
    }
}