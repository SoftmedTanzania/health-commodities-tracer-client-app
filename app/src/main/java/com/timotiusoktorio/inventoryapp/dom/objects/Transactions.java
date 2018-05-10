package com.timotiusoktorio.inventoryapp.dom.objects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by issy on 11/23/17.
 */


@Entity
public class Transactions implements Serializable {

    @SerializedName("id")
    private int id;


    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("uid")
    private String uid;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("type")
    private String type;

    @SerializedName("amount")
    private int amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
