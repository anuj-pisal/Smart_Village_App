package com.example.smartvillageapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.data.gov.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}