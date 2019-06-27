package com.softmed.stockapp.Dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class ProductsResponse implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("generic_name")
    private String name;

    @SerializedName("brand_name")
    private String description;

    @SerializedName("unit")
    private int unit;

    @SerializedName("health_commodity_category")
    int category_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
