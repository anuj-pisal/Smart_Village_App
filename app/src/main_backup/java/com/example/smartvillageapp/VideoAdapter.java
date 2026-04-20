package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    List<VideoModel> list;

    public VideoAdapter(Context context, List<VideoModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView thumbnail;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.video_title);
            thumbnail = v.findViewById(R.id.video_thumbnail);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        VideoModel v = list.get(i);

        h.title.setText(v.title);

        String videoId = "";

        if (v.videoUrl != null) {

            if (v.videoUrl.contains("v=")) {
                videoId = v.videoUrl.split("v=")[1];

                int ampIndex = videoId.indexOf("&");
                if (ampIndex != -1) {
                    videoId = videoId.substring(0, ampIndex);
                }

            } else if (v.videoUrl.contains("youtu.be/")) {
                videoId = v.videoUrl.substring(v.videoUrl.lastIndexOf("/") + 1);

            } else if (v.videoUrl.contains("/live/")) {
                videoId = v.videoUrl.substring(v.videoUrl.lastIndexOf("/") + 1);
            }

            int qIndex = videoId.indexOf("?");
            if (qIndex != -1) {
                videoId = videoId.substring(0, qIndex);
            }
        }

        // fallback safety
        if (!videoId.isEmpty()) {
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";

            Glide.with(context)
                    .load(thumbnailUrl)
                    .into(h.thumbnail);
        }

        // 🔥 OPEN YOUTUBE
        h.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(v.videoUrl));
            context.startActivity(intent);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_video, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}