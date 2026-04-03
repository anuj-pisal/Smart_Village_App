package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DevelopmentAdapter extends RecyclerView.Adapter<DevelopmentAdapter.ViewHolder> {

    Context context;
    List<DevelopmentModel> list;

    public DevelopmentAdapter(Context context, List<DevelopmentModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, shortDesc;
        ImageView image;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.dev_title);
            shortDesc = v.findViewById(R.id.dev_short);
            image = v.findViewById(R.id.dev_image);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        DevelopmentModel d = list.get(i);

        h.title.setText(d.title);
        h.shortDesc.setText(d.shortDesc);

        // show first image
        Glide.with(context)
                .load(d.images.get(0))
                .into(h.image);

        // 🔥 OPEN DETAIL
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DevelopmentDetailActivity.class);
            intent.putExtra("devId", d.id);
            context.startActivity(intent);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_development, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
