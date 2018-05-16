package com.timotiusoktorio.inventoryapp.dom.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductBalance implements Serializable {

    @SerializedName("subCategoryName")
    private String subCategoryName;

    @SerializedName("productName")
    private String productName;


    @SerializedName("unit")
    private String unit;

    @SerializedName("balance")
    private int balance;

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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
