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

            switch (item.getTitle()) {

                case "About Village":
                    intent = new Intent(context, AboutVillageActivity.class);
                    break;

                case "Contacts":
                    intent = new Intent(context, ContactsActivity.class);
                    break;
//
                case "Businesses":
                    intent = new Intent(context, BusinessActivity.class);
                    break;

                case "Market Prices":
                    intent = new Intent(context, MarketActivity.class);
                    break;
//
                case "Notices":
                    intent = new Intent(context, NoticesActivity.class);
                    break;
//
                case "Bills":
                    intent = new Intent(context, BillsActivity.class);
                    break;

                case "Locations":
                    intent = new Intent(context, LocationsActivity.class);
                    break;
//
                case "Complaints":
                    intent = new Intent(context, ComplaintsActivity.class);
                    break;
//
                case "Schemes":
                    intent = new Intent(context, SchemesActivity.class);
                    break;
//
                case "Developments":
                    intent = new Intent(context, DevelopmentActivity.class);
                    break;

                case "Agricultural":
                    intent = new Intent(context, AgriculturalActivity.class);
                    break;
//
                case "Jobs":
                    intent = new Intent(context, JobsActivity.class);
                    break;
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
