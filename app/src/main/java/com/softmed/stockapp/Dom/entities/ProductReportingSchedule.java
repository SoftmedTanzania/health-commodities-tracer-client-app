package com.softmed.stockapp.Dom.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class ProductReportingSchedule implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("health_commodity")
    private int product_id;

    @SerializedName("health_facility")
    private int facility_id;

    @SerializedName("scheduled_date")
    private long scheduled_date;

    @SerializedName("status")
    private String status;


    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(int facility_id) {
        this.facility_id = facility_id;
    }

    public long getScheduled_date() {
        return scheduled_date;
    }

    public void setScheduled_date(long scheduled_date) {
        this.scheduled_date = scheduled_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
