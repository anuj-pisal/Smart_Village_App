package com.example.smartvillageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminLogsAdapter extends RecyclerView.Adapter<AdminLogsAdapter.VH> {

    Context context;
    List<ActivityLogModel> list;

    public AdminLogsAdapter(Context context, List<ActivityLogModel> list) {
        this.context = context;
        this.list = list;
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView action, details, user, time;

        public VH(View v) {
            super(v);
            action = v.findViewById(R.id.action);
            details = v.findViewById(R.id.details);
            user = v.findViewById(R.id.user);
            time = v.findViewById(R.id.time);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        ActivityLogModel l = list.get(i);

        h.action.setText(l.action);
        h.details.setText(l.details);
        h.user.setText("Users : " + l.userId);

        SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault());
        h.time.setText(f.format(new Date(l.timestamp)));
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_log, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}