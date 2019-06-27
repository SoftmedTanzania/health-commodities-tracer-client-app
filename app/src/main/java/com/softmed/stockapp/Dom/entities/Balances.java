package com.softmed.stockapp.Dom.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class Balances implements Serializable {


    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("user_created")
    private int user_id;

    @SerializedName("health_facility")
    private int health_facility_id;

    @SerializedName("health_commodity")
    private int product_id;

    @SerializedName("quantity_available")
    private int numberOfClientsOnRegime;

    @SerializedName("balance")
    private int balance;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getHealth_facility_id() {
        return health_facility_id;
    }

    public void setHealth_facility_id(int health_facility_id) {
        this.health_facility_id = health_facility_id;
    }

    public int getNumberOfClientsOnRegime() {
        return numberOfClientsOnRegime;
    }

    public void setNumberOfClientsOnRegime(int numberOfClientsOnRegime) {
        this.numberOfClientsOnRegime = numberOfClientsOnRegime;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}
