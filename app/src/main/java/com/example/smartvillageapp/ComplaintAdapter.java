package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.VH> {

    Context context;
    List<ComplaintModel> list;

    public ComplaintAdapter(Context c, List<ComplaintModel> l) {
        context = c;
        list = l;
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView desc, status;

        public VH(View v) {
            super(v);
            img = v.findViewById(R.id.img);
            desc = v.findViewById(R.id.desc);
            status = v.findViewById(R.id.status);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {
        ComplaintModel c = list.get(i);

        h.desc.setText(c.description);

        Glide.with(context).load(c.imageUrl).into(h.img);

        h.status.setText(context.getString(R.string.status_prefix) + c.status);

        if ("pending".equals(c.status))
            h.status.setTextColor(Color.YELLOW);
        else if ("in_progress".equals(c.status))
            h.status.setTextColor(Color.parseColor("#00ccff"));
        else
            h.status.setTextColor(Color.GREEN);

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ComplaintDetailActivity.class);
            intent.putExtra("id", c.id);
            context.startActivity(intent);
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_complaint, p, false));
    }

    @Override
    public int getItemCount() { return list.size(); }
}