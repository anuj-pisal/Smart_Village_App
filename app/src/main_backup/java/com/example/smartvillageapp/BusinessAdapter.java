package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    private Context context;
    private List<BusinessModel> businessList;

    public BusinessAdapter(Context context, List<BusinessModel> businessList) {
        this.context = context;
        this.businessList = businessList;
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {

        BusinessModel business = businessList.get(position);

        holder.name.setText(business.getName());
        holder.domain.setText(business.getDomain());
        holder.address.setText(business.getAddress());
        holder.description.setText(business.getDescription());
        holder.email.setText(business.getEmail());

        // Load image safely with Glide
        Glide.with(context)
                .load(business.getImageUrl())
                .placeholder(R.drawable.slide2)
                .into(holder.image);

        // Click phone → open dialer
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + business.getPhone()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    static class BusinessViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, domain, address, description, email;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.business_image);
            name = itemView.findViewById(R.id.business_name);
            domain = itemView.findViewById(R.id.business_domain);
            address = itemView.findViewById(R.id.business_address);
            description = itemView.findViewById(R.id.business_description);
            email = itemView.findViewById(R.id.business_email);
        }
    }
}
