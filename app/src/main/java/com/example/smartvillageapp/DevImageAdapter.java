package com.example.smartvillageapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DevImageAdapter extends RecyclerView.Adapter<DevImageAdapter.ViewHolder> {

    List<String> images;

    public DevImageAdapter(List<String> images) {
        this.images = images;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.slider_image);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        Glide.with(h.image.getContext())
                .load(images.get(i))
                .into(h.image);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_slider_image, p, false));
    }

    @Override
    public int getItemCount() { return images.size(); }
}