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
        Fragment fragment;
        if (position == 0)
            fragment = new CropFragment();
        else
            fragment = new VideoFragment();

        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putBoolean("isAdmin", true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}