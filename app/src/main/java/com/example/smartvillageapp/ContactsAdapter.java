package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private List<ContactsModel> list;

    public ContactsAdapter(Context context, List<ContactsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ContactsModel model = list.get(position);

        holder.name.setText(model.getName());
        holder.designation.setText(model.getDesignation());
        holder.phone.setText(model.getPhone());
        holder.email.setText(model.getEmail());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + model.getPhone()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, designation, phone, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contact_name);
            designation = itemView.findViewById(R.id.contact_designation);
            phone = itemView.findViewById(R.id.contact_phone);
            email = itemView.findViewById(R.id.contact_email);
        }
    }
}

