package com.softmed.stockapp_staging.dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ProductsPostResponse implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("entity")
    private String uuid;

    @SerializedName("action")
    private String action;

    @SerializedName("type")
    private String type;

    @SerializedName("status")
    private String status;

    @SerializedName("user")
    private int userId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
