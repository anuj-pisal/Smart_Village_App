package com.example.smartvillageapp;

import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.VH> {

    Context context;
    List<BillModel> list;

    public BillAdapter(Context c, List<BillModel> l) {
        context = c;
        list = l;
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, amount, date, status;

        public VH(View v) {
            super(v);
            img = v.findViewById(R.id.img);
            title = v.findViewById(R.id.title);
            amount = v.findViewById(R.id.amount);
            date = v.findViewById(R.id.date);
            status = v.findViewById(R.id.status);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        BillModel b = list.get(i);

        h.title.setText(b.title);
        h.amount.setText("₹" + b.amount);
        h.date.setText(b.dueDate);
        h.status.setText(b.status.toUpperCase());

        if ("unpaid".equals(b.status))
            h.status.setTextColor(Color.RED);
        else
            h.status.setTextColor(Color.GREEN);

        Glide.with(context).load(b.images.get(0)).into(h.img);

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BillDetailActivity.class);
            intent.putExtra("id", b.id);
            context.startActivity(intent);
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_bill, p, false));
    }

    @Override
    public int getItemCount() { return list.size(); }
}