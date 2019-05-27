package com.softmed.stockapp.Dom.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionSummary implements Serializable {

    @SerializedName("subCategoryName")
    private String subCategoryName;

    @SerializedName("productName")
    private String productName;


    @SerializedName("transactionType")
    private String transactionType;

    @SerializedName("amount")
    private int amount;

    @SerializedName("price")
    private int price;

    @SerializedName("created_at")
    private long created_at;

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
