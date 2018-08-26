package com.softmed.rucodia.Dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BalancePricesResponse implements Serializable {

    @SerializedName("product_id")
    private int productId;

    @SerializedName("buying_price")
    private int buyingPrice;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }
}
