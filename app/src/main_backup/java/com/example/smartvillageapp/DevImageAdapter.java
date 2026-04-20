package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DevImageAdapter extends RecyclerView.Adapter<DevImageAdapter.ViewHolder> {

    List<String> images;
    Context context;

    public DevImageAdapter(Context context, List<String> images) {
        this.images = images;
        this.context = context;
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

        String url = images.get(i);

        Glide.with(context)
                .load(url)
                .into(h.image);

        // 🔥 CLICK → FULL SCREEN IMAGE
        h.image.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullImageActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_slider_image, p, false));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}