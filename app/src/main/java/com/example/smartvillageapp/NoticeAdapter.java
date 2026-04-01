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

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    Context context;
    List<NoticeModel> list;

    public NoticeAdapter(Context context, List<NoticeModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.notice_title);
            image = v.findViewById(R.id.notice_image);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        NoticeModel n = list.get(i);

        h.title.setText(n.title);

        Glide.with(context)
                .load(n.imageUrl)
                .centerCrop()
                .thumbnail(0.3f)
                .into(h.image);

        // 🔥 OPEN FULL IMAGE
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoticeDetailActivity.class);
            intent.putExtra("imageUrl", n.imageUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_notice, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}