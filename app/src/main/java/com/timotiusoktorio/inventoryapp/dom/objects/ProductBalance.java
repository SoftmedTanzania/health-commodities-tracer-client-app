package com.timotiusoktorio.inventoryapp.dom.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductBalance implements Serializable {

    @SerializedName("subCategoryName")
    private String subCategoryName;

    @SerializedName("productName")
    private String productName;


    @SerializedName("productDescription")
    private String productDescription;


    @SerializedName("unit")
    private String unit;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("balance")
    private int balance;

    @SerializedName("price")
    private int price;

    @SerializedName("productId")
    private int productId;

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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
