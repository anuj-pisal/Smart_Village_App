package com.example.smartvillageapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ImageView appLogo = view.findViewById(R.id.app_logo);

        Glide.with(this)
                .load(R.drawable.app_logo_3)
                .circleCrop()
                .into(appLogo);

        return view;
    }
}