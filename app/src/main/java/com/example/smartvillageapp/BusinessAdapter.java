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

        // Long click -> context menu
        holder.itemView.setOnLongClickListener(v -> {
            android.app.Dialog dialog = new android.app.Dialog(context);
            dialog.setContentView(R.layout.dialog_choose_action);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            android.widget.TextView title = dialog.findViewById(R.id.dialog_title);
            android.widget.TextView subtitle = dialog.findViewById(R.id.dialog_subtitle);
            title.setText(context.getString(R.string.choose_action));
            subtitle.setVisibility(View.VISIBLE);
            subtitle.setText(business.getName());

            dialog.findViewById(R.id.btn_call).setOnClickListener(btnView -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + business.getPhone()));
                context.startActivity(intent);
                dialog.dismiss();
            });

            dialog.findViewById(R.id.btn_mail).setOnClickListener(btnView -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + business.getEmail()));
                context.startActivity(intent);
                dialog.dismiss();
            });

            dialog.show();
            return true;
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
