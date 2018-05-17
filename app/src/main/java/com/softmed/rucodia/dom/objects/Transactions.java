package com.softmed.rucodia.dom.objects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class Transactions implements Serializable {

    @SerializedName("id")
    private int id;

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("uuid")
    private String uuid;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("transactiontype_id")
    private int transactiontype_id;

    @SerializedName("product_id")
    private int product_id;

    @SerializedName("status_id")
    private int status_id;

    @SerializedName("amount")
    private int amount;

    @SerializedName("price")
    private int price;

    @SerializedName("syncStatus")
    private int syncStatus = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
