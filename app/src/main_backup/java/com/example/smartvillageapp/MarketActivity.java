package com.example.smartvillageapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import java.util.*;

import retrofit2.*;

public class MarketActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<MarketModel> list = new ArrayList<>();
    MarketAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_market);

        recycler = findViewById(R.id.market_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MarketAdapter(this, list);
        recycler.setAdapter(adapter);

        // 🔥 STEP 1: CHECK CACHE FIRST
        if (MarketCache.cachedList != null && !MarketCache.cachedList.isEmpty()) {

            list.clear();
            list.addAll(MarketCache.cachedList);
            adapter.notifyDataSetChanged();


        } else {
            loadData();
        }
    }

    private void loadData() {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getPrices(
                "579b464db66ec23bdd000001649bf664fa794e14784994c2e1080dd8",
                "json",
                5000,
                "Maharashtra",
                "Sangli",
                null,
                null
        ).enqueue(new Callback<MarketResponse>() {

            @Override
            public void onResponse(Call<MarketResponse> call, Response<MarketResponse> response) {

                if (response.body() != null && response.body().records != null) {

                    List<MarketModel> raw = response.body().records;

                    // 🔥 Map → commodity → list of prices
                    Map<String, List<Double>> priceMap = new HashMap<>();

                    for (MarketModel m : raw) {
                        try {
                            if (m.commodity == null || m.modal_price == null) continue;

                            double price = Double.parseDouble(m.modal_price);

                            priceMap.putIfAbsent(m.commodity, new ArrayList<>());
                            priceMap.get(m.commodity).add(price);

                        } catch (Exception ignored) {}
                    }

                    list.clear();

                    for (String commodity : priceMap.keySet()) {

                        List<Double> prices = priceMap.get(commodity);

                        double min = Collections.min(prices);
                        double max = Collections.max(prices);

                        MarketModel model = new MarketModel();
                        model.commodity = commodity;
                        model.min_price = String.valueOf(min);
                        model.max_price = String.valueOf(max);
                        model.market = "Multiple Markets";
                        model.district = "Sangli";

                        list.add(model);
                    }

                    // 🔥 Sort alphabetically
                    Collections.sort(list, (a, b) -> a.commodity.compareTo(b.commodity));

                    adapter.notifyDataSetChanged();

                    // 🔥 STEP 3: SAVE TO MEMORY CACHE
                    MarketCache.cachedList = new ArrayList<>(list);


                } else {
                    Toast.makeText(MarketActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MarketResponse> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "API failed", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}