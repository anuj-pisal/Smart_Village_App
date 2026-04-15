package com.example.smartvillageapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BillUserAdapter extends RecyclerView.Adapter<BillUserAdapter.VH> {

    Context context;
    List<BillModel> list;

    public BillUserAdapter(Context context, List<BillModel> list) {
        this.context = context;
        this.list = list;
    }

    public class VH extends RecyclerView.ViewHolder {

        TextView title, amount, status, date;

        public VH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            amount = v.findViewById(R.id.amount);
            status = v.findViewById(R.id.status);
            date = v.findViewById(R.id.date);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_user_bill, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {

        BillModel b = list.get(position); // 🔥 FIX

        h.title.setText(b.title);
        h.amount.setText("₹" + b.amount);
        h.status.setText(b.status);
        h.date.setText(b.dueDate);

        if ("unpaid".equalsIgnoreCase(b.status)) {
            h.status.setTextColor(Color.RED);
        } else {
            h.status.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}