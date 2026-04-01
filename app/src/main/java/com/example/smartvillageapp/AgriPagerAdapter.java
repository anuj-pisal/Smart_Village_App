package com.example.smartvillageapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AgriPagerAdapter extends FragmentStateAdapter {

    public AgriPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0: return new CropFragment();
            case 1: return new ExperienceFragment();
            case 2: return new VideoFragment();
        }

        return new CropFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
