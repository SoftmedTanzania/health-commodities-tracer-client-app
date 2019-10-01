package com.softmed.stockapp_staging.dom.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class MappedProductConsuption implements Serializable {

    private int productId;

    private int consumption;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getConsumption() {
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }
}
