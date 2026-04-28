package com.example.smartvillageapp;

import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.VH> {

    Context context;
    List<MarketModel> list;

    public MarketAdapter(Context c, List<MarketModel> l) {
        context = c;
        list = l;
    }

    public void filterList(List<MarketModel> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder {

        TextView crop, price, market;

        public VH(View v) {
            super(v);
            crop = v.findViewById(R.id.crop);
            price = v.findViewById(R.id.price);
            market = v.findViewById(R.id.market);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        MarketModel m = list.get(i);

        h.crop.setText(m.commodity);
        h.market.setText(context.getString(R.string.across_prefix) + m.district + " markets");

        // 🔥 Convert ₹/quintal → ₹/kg
        try {
            double min = Double.parseDouble(m.min_price) / 100.0;
            double max = Double.parseDouble(m.max_price) / 100.0;
            double avg = (min + max) / 2.0;

            String formattedValue = String.format(Locale.getDefault(), "Min: %.0f | Max: %.0f | Avg: %.0f", min, max, avg);
            h.price.setText(formattedValue);

        } catch (Exception e) {
            h.price.setText(context.getString(R.string.rupee_placeholder));
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.item_market, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}