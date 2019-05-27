package com.softmed.stockapp.Dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class ProductsResponse implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("units")
    List<UnitsResponse> unitResponses;

    @SerializedName("category_id")
    int category_id;

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

    public List<UnitsResponse> getUnitResponses() {
        return unitResponses;
    }

    public void setUnitResponses(List<UnitsResponse> unitResponses) {
        this.unitResponses = unitResponses;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
