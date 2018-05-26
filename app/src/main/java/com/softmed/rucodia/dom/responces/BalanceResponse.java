package com.softmed.rucodia.dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BalanceResponse implements Serializable {

    @SerializedName("product_id")
    private int productId;

    @SerializedName("product_balance")
    private int balance;

    @SerializedName("product_price")
    private int price;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
