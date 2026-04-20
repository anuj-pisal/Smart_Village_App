package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder> {

    Context context;
    List<ExperienceModel> list;

    public ExperienceAdapter(Context context, List<ExperienceModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, user;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.exp_title);
            desc = v.findViewById(R.id.exp_desc);
            user = v.findViewById(R.id.exp_user);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        ExperienceModel e = list.get(i);

        h.title.setText(e.title);
        h.desc.setText(e.description);
        h.user.setText(context.getString(R.string.by_prefix) + e.userName);

        // 🔥 OPEN DETAIL SCREEN
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExperienceDetailActivity.class);
            intent.putExtra("expId", e.id);
            intent.putExtra("postingUser", e.userName);
            context.startActivity(intent);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_experience, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}