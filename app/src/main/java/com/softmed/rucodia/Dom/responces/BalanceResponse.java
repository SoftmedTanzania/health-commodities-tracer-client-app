package com.softmed.rucodia.Dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BalanceResponse implements Serializable {

    @SerializedName("product_id")
    private int productId;

    @SerializedName("product_balance")
    private int balance;


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
}
