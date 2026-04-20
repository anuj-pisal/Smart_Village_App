package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminDashboardAdapter extends RecyclerView.Adapter<AdminDashboardAdapter.VH> {

    Context context;
    List<AdminItem> list;

    public AdminDashboardAdapter(Context c, List<AdminItem> l) {
        context = c;
        list = l;
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public VH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            icon = v.findViewById(R.id.adminDashboardIcon);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        AdminItem item = list.get(i);
        h.title.setText(item.title);
        h.icon.setImageResource(item.iconResId);

        h.itemView.setOnClickListener(v -> {

            
            String t = item.title;
            if (t.equals(context.getString(R.string.contacts))) {
                context.startActivity(new Intent(context, AddContactActivity.class));
            } else if (t.equals(context.getString(R.string.notices))) {
                context.startActivity(new Intent(context, AddNoticeActivity.class));
            } else if (t.equals(context.getString(R.string.bills))) {
                context.startActivity(new Intent(context, BillsUserListActivity.class));
            } else if (t.equals(context.getString(R.string.locations))) {
                context.startActivity(new Intent(context, AddLocationActivity.class));
            } else if (t.equals(context.getString(R.string.complaints))) {
                context.startActivity(new Intent(context, AdminComplaintActivity.class));
            } else if (t.equals(context.getString(R.string.schemes))) {
                context.startActivity(new Intent(context, AddSchemeActivity.class));
            } else if (t.equals(context.getString(R.string.developments))) {
                context.startActivity(new Intent(context, AddDevelopmentActivity.class));
            } else if (t.equals(context.getString(R.string.agriculture))) {
                context.startActivity(new Intent(context, AgricultureActivity.class));
            } else if (t.equals(context.getString(R.string.users))) {
                context.startActivity(new Intent(context, AdminUserManagementActivity.class));
            } else if (t.equals(context.getString(R.string.logs))) {
                context.startActivity(new Intent(context, AdminLogsActivity.class));
            }

        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.item_admin_dashboard, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}