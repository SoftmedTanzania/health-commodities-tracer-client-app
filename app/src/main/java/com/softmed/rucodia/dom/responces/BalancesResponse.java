package com.softmed.rucodia.dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class BalancesResponse implements Serializable {

    @SerializedName("action")
    private String action;

    @SerializedName("user")
    private int userId;

    @SerializedName("status")
    private String status;

    @SerializedName("entity")
    private String entity;

    @SerializedName("type")
    private String type;

    @SerializedName("products")
    private List<BalanceResponse> products;

    @SerializedName("prices")
    private List<BalancePricesResponse> prices;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BalanceResponse> getProducts() {
        return products;
    }

    public void setProducts(List<BalanceResponse> products) {
        this.products = products;
    }

    public List<BalancePricesResponse> getPrices() {
        return prices;
    }

    public void setPrices(List<BalancePricesResponse> prices) {
        this.prices = prices;
    }
}
