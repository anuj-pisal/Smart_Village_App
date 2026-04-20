package com.example.smartvillageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AgriculturePagerAdapter extends FragmentStateAdapter {

    public AgriculturePagerAdapter(AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new CropAdminFragment();
        else
            return new VideoAdminFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}