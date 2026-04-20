package com.example.smartvillageapp;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketPrefetcher {

    public static void fetchAndCache() {
        // Prevent duplicate calls if already cached
        if (MarketCache.cachedList != null && !MarketCache.cachedList.isEmpty()) {
            return;
        }

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
                    Map<String, List<Double>> priceMap = new HashMap<>();

                    for (MarketModel m : raw) {
                        try {
                            if (m.commodity == null || m.modal_price == null) continue;
                            double price = Double.parseDouble(m.modal_price);
                            priceMap.putIfAbsent(m.commodity, new ArrayList<>());
                            priceMap.get(m.commodity).add(price);
                        } catch (Exception ignored) {}
                    }

                    List<MarketModel> parsedList = new ArrayList<>();
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
                        parsedList.add(model);
                    }

                    Collections.sort(parsedList, (a, b) -> a.commodity.compareTo(b.commodity));
                    
                    // Save silently in the background
                    MarketCache.cachedList = parsedList;
                    Log.d("MarketPrefetcher", "Market data successfully prefetched!");
                }
            }

            @Override
            public void onFailure(Call<MarketResponse> call, Throwable t) {
                Log.e("MarketPrefetcher", "Prefetch failed: " + t.getMessage());
            }
        });
    }
}
