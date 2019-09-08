package com.softmed.stockapp_staging.dom.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class Transactions implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private String id;

    @SerializedName("user_created")
    private int user_id;

    @SerializedName("posting_schedule")
    private int scheduleId;

    @SerializedName("transactiontype_id")
    private int transactiontype_id;

    @SerializedName("product_id")
    private int product_id;

    @SerializedName("status_id")
    private int status_id;

    @SerializedName("quantity_available")
    private int amount;

    @SerializedName("has_patients")
    private boolean hasClients;

    @SerializedName("number_of_clients")
    private String clientsOnRegime;

    @SerializedName("quantity_wasted")
    private String wastage;

    @SerializedName("quantity_expired")
    private String quantityExpired;

    @SerializedName("stock_out_days")
    private int stockOutDays;

    @SerializedName("syncStatus")
    private int syncStatus = 0;


    @SerializedName("quantity_consumed")
    private int consumptionQuantity;

    @SerializedName("trans_date_time")
    private long created_at;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTransactiontype_id() {
        return transactiontype_id;
    }

    public void setTransactiontype_id(int transactiontype_id) {
        this.transactiontype_id = transactiontype_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getClientsOnRegime() {
        return clientsOnRegime;
    }

    public void setClientsOnRegime(String clientsOnRegime) {
        this.clientsOnRegime = clientsOnRegime;
    }

    public String getWastage() {
        return wastage;
    }

    public void setWastage(String wastage) {
        this.wastage = wastage;
    }

    public String getQuantityExpired() {
        return quantityExpired;
    }

    public void setQuantityExpired(String quantityExpired) {
        this.quantityExpired = quantityExpired;
    }

    public int getStockOutDays() {
        return stockOutDays;
    }

    public void setStockOutDays(int stockOutDays) {
        this.stockOutDays = stockOutDays;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public boolean isHasClients() {
        return hasClients;
    }

    public void setHasClients(boolean hasClients) {
        this.hasClients = hasClients;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getConsumptionQuantity() {
        return consumptionQuantity;
    }

    public void setConsumptionQuantity(int consumptionQuantity) {
        this.consumptionQuantity = consumptionQuantity;
    }
}
