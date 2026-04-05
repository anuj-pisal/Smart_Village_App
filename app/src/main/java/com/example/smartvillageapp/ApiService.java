package com.example.smartvillageapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("resource/35985678-0d79-46b4-9ed6-6f13308a1d24")
    Call<MarketResponse> getPrices(
            @Query("api-key") String key,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("filters[State]") String state,
            @Query("filters[District]") String district,
            @Query("filters[Commodity]") String commodity,
            @Query("filters[Arrival_Date]") String date
    );
}