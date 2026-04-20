package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private List<DashboardItem> list;
    private Context context;

    public DashboardAdapter(Context context, List<DashboardItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardItem item = list.get(position);
        holder.icon.setImageResource(item.getIcon());
        holder.title.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();
            Intent intent = null;

            
            String title = item.getTitle();
            if (title.equals(context.getString(R.string.about_village))) {
                intent = new Intent(context, AboutVillageActivity.class);
            } else if (title.equals(context.getString(R.string.contacts))) {
                intent = new Intent(context, ContactsActivity.class);
            } else if (title.equals(context.getString(R.string.businesses))) {
                intent = new Intent(context, BusinessActivity.class);
            } else if (title.equals(context.getString(R.string.market_prices))) {
                intent = new Intent(context, MarketActivity.class);
            } else if (title.equals(context.getString(R.string.notices))) {
                intent = new Intent(context, NoticesActivity.class);
            } else if (title.equals(context.getString(R.string.bills))) {
                intent = new Intent(context, BillsActivity.class);
            } else if (title.equals(context.getString(R.string.locations))) {
                intent = new Intent(context, LocationsActivity.class);
            } else if (title.equals(context.getString(R.string.complaints))) {
                intent = new Intent(context, ComplaintsActivity.class);
            } else if (title.equals(context.getString(R.string.schemes))) {
                intent = new Intent(context, SchemesActivity.class);
            } else if (title.equals(context.getString(R.string.developments))) {
                intent = new Intent(context, DevelopmentActivity.class);
            } else if (title.equals(context.getString(R.string.agricultural))) {
                intent = new Intent(context, AgriculturalActivity.class);
            } else if (title.equals(context.getString(R.string.jobs))) {
                intent = new Intent(context, JobsActivity.class);
            }


            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.dashboardIcon);
            title = itemView.findViewById(R.id.dashboardTitle);
        }
    }
}
