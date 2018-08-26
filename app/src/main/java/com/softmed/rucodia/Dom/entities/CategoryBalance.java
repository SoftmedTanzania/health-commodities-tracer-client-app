package com.softmed.rucodia.Dom.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryBalance implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("balance")
    private int balance;

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
}
