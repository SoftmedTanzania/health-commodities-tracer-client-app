package com.softmed.stockapp.Dom.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class OrdersItems implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("ordered")
    private int ordered;

    @SerializedName("prices")
    private int prices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public int getPrices() {
        return prices;
    }

    public void setPrices(int prices) {
        this.prices = prices;
    }
}
