package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    List<LocationModel> list;
    Context context;

    public LocationAdapter(Context context, List<LocationModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, desc;
        Button btn;

        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.location_image);
            name = v.findViewById(R.id.location_name);
            desc = v.findViewById(R.id.location_desc);
            btn = v.findViewById(R.id.open_map_btn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LocationModel model = list.get(position);

        holder.name.setText(model.name);
        holder.desc.setText(model.description);

        // 🔥 Load image safely
        Glide.with(context)
                .load(model.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        holder.btn.setOnClickListener(v -> {
            String uri = "geo:" + model.latitude + "," + model.longitude +
                    "?q=" + model.latitude + "," + model.longitude;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
