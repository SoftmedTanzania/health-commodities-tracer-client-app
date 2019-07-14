package com.softmed.stockapp.dom.entities;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(primaryKeys = {"healthFacilityId", "productId"})
public class Balances implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("user_created")
    private int userId;

    @SerializedName("location")
    private int healthFacilityId;

    @SerializedName("health_commodity")
    private int productId;

    @SerializedName("quantity_available")
    private int balance;

    @SerializedName("quantity_consumed")
    private int consumptionQuantity;

    @SerializedName("sync_status")
    private int syncStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHealthFacilityId() {
        return healthFacilityId;
    }

    public void setHealthFacilityId(int healthFacilityId) {
        this.healthFacilityId = healthFacilityId;
    }

    public int getConsumptionQuantity() {
        return consumptionQuantity;
    }

    public void setConsumptionQuantity(int consumptionQuantity) {
        this.consumptionQuantity = consumptionQuantity;
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

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
