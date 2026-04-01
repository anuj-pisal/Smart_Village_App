package com.example.smartvillageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.ViewHolder> {

    Context context;
    List<CropModel> list;

    public CropAdapter(Context context, List<CropModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc;
        ImageView image;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.crop_title);
            desc = v.findViewById(R.id.crop_desc);
            image = v.findViewById(R.id.crop_image);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        CropModel c = list.get(i);

        h.title.setText(c.title);
        h.desc.setText(c.description);

        Glide.with(context)
                .load(c.imageUrl)
                .centerCrop()
                .into(h.image);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_crop, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}