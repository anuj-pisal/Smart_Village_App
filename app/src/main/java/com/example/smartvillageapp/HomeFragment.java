package com.example.smartvillageapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private RecyclerView recyclerView;

    // 🔥 Auto slider handler
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    private List<Integer> images;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.imageSlider);
        recyclerView = view.findViewById(R.id.dashboardRecycler);

        setupSlider();
        setupGrid();
    }

    private void setupSlider() {

        images = new ArrayList<>();

        // Duplicate first & last for smooth infinite illusion
        images.add(R.drawable.slide6);
        images.add(R.drawable.slide1);
        images.add(R.drawable.slide2);
        images.add(R.drawable.slide3);
        images.add(R.drawable.slide4);
        images.add(R.drawable.slide5);
        images.add(R.drawable.slide6);
        images.add(R.drawable.slide1);

        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(adapter);

        // Start from real first item
        viewPager.setCurrentItem(1, false);

        // Infinite loop logic
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {

                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(images.size() - 2, false);
                    }

                    if (viewPager.getCurrentItem() == images.size() - 1) {
                        viewPager.setCurrentItem(1, false);
                    }
                }
            }
        });

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = viewPager.getCurrentItem() + 1;

                // Smooth scroll enabled
                viewPager.setCurrentItem(nextItem, true);

                sliderHandler.postDelayed(this, 3000); // 3 sec delay
            }
        };
    }

    private void setupGrid() {

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<DashboardItem> list = new ArrayList<>();

        list.add(new DashboardItem(R.drawable.home, getString(R.string.about_village)));
        list.add(new DashboardItem(R.drawable.contacts, getString(R.string.contacts)));
        list.add(new DashboardItem(R.drawable.business, getString(R.string.businesses)));
        list.add(new DashboardItem(R.drawable.market_prices, getString(R.string.market_prices)));
        list.add(new DashboardItem(R.drawable.notices, getString(R.string.notices)));
        list.add(new DashboardItem(R.drawable.bill, getString(R.string.bills)));
        list.add(new DashboardItem(R.drawable.locations, getString(R.string.locations)));
        list.add(new DashboardItem(R.drawable.complaints, getString(R.string.complaints)));
        list.add(new DashboardItem(R.drawable.schemes, getString(R.string.schemes)));
        list.add(new DashboardItem(R.drawable.development, getString(R.string.developments)));
        list.add(new DashboardItem(R.drawable.agriculture, getString(R.string.agricultural)));
        list.add(new DashboardItem(R.drawable.jobs, getString(R.string.jobs)));

        DashboardAdapter adapter = new DashboardAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}