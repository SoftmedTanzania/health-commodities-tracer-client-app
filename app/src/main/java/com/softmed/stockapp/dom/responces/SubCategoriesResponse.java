package com.softmed.stockapp.dom.responces;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class SubCategoriesResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @NonNull
    @SerializedName("uuid")
    private String uuid;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
