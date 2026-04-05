package com.example.smartvillageapp;

import com.google.gson.annotations.SerializedName;

public class MarketModel {

    @SerializedName("State")
    public String state;

    @SerializedName("District")
    public String district;

    @SerializedName("Market")
    public String market;

    @SerializedName("Commodity")
    public String commodity;

    @SerializedName("Modal_Price")
    public String modal_price;

    public String min_price;
    public String max_price;
}