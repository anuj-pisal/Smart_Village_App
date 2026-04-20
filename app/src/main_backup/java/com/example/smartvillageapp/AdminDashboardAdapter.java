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

            switch (item.title) {

                case "Contacts":
                    context.startActivity(new Intent(context, AddContactActivity.class));
                    break;

                case "Notices":
                    context.startActivity(new Intent(context, AddNoticeActivity.class));
                    break;

                case "Bills":
                    context.startActivity(new Intent(context, BillsUserListActivity.class));
                    break;

                case "Locations":
                    context.startActivity(new Intent(context, AddLocationActivity.class));
                    break;

                case "Complaints":
                    context.startActivity(new Intent(context, AdminComplaintActivity.class));
                    break;

                case "Schemes":
                    context.startActivity(new Intent(context, AddSchemeActivity.class));
                    break;

                case "Developments":
                    context.startActivity(new Intent(context, AddDevelopmentActivity.class));
                    break;

                case "Agriculture":
                    context.startActivity(new Intent(context, AgricultureActivity.class));
                    break;

                case "Users":
                    context.startActivity(new Intent(context, AdminUserManagementActivity.class));
                    break;

                case "Logs":
                    context.startActivity(new Intent(context, AdminLogsActivity.class));
                    break;

                case "संपर्क":
                    context.startActivity(new Intent(context, AddContactActivity.class));
                    break;

                case "सूचना":
                    context.startActivity(new Intent(context, AddNoticeActivity.class));
                    break;

                case "बिले":
                    context.startActivity(new Intent(context, BillsUserListActivity.class));
                    break;

                case "ठिकाणे":
                    context.startActivity(new Intent(context, AddLocationActivity.class));
                    break;

                case "तक्रारी":
                    context.startActivity(new Intent(context, AdminComplaintActivity.class));
                    break;

                case "योजना":
                    context.startActivity(new Intent(context, AddSchemeActivity.class));
                    break;

                case "विकासकामे":
                    context.startActivity(new Intent(context, AddDevelopmentActivity.class));
                    break;

                case "शेती":
                    context.startActivity(new Intent(context, AgricultureActivity.class));
                    break;

                case "वापरकर्ते":
                    context.startActivity(new Intent(context, AdminUserManagementActivity.class));
                    break;

                case "नोंदी":
                    context.startActivity(new Intent(context, AdminLogsActivity.class));
                    break;
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