package com.softmed.stockapp.dom.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryBalance implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("balance")
    private int balance;

    private int consumptionQuantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getConsumptionQuantity() {
        return consumptionQuantity;
    }

    public void setConsumptionQuantity(int consumptionQuantity) {
        this.consumptionQuantity = consumptionQuantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
