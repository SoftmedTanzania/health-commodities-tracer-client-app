package com.softmed.stockapp.Dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class CategoriesResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("health_commodity_category_description")
    private String name;

    @SerializedName("health_commodity_category_name")
    private String description;

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


}
