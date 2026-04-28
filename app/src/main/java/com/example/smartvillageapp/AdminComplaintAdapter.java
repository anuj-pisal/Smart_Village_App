package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminComplaintAdapter extends RecyclerView.Adapter<AdminComplaintAdapter.VH> {

    Context context;
    List<AdminComplaintModel> list;

    public AdminComplaintAdapter(Context context, List<AdminComplaintModel> list) {
        this.context = context;
        this.list = list;
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView desc, status, user;
        ImageView img;

        public VH(View v) {
            super(v);
            desc = v.findViewById(R.id.desc);
            status = v.findViewById(R.id.status);
            user = v.findViewById(R.id.user);
            img = v.findViewById(R.id.img);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        AdminComplaintModel c = list.get(i);

        h.desc.setText(c.description);
        h.status.setText(context.getString(R.string.status_prefix) + c.status);

        Glide.with(context).load(c.imageUrl).into(h.img);

        // 🔥 FETCH USERNAME USING userId
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(c.userId)
                .get()
                .addOnSuccessListener(doc -> {
                    h.user.setText(doc.getString("username"));
                });

        // CLICK → OPEN DETAIL
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminComplaintDetailActivity.class);
            intent.putExtra("docId", c.docId);
            intent.putExtra("userId", c.userId);
            intent.putExtra("username", h.user.getText().toString());
            intent.putExtra("imageUrl", c.imageUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_admin_complaint, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}