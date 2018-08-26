package com.softmed.rucodia.Dom.responces;

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

    @SerializedName("price")
    private int price;

    @SerializedName("units")
    List<UnitsResponse> unitResponses;

    @SerializedName("subcategories")
    List<SubCategoriesResponse> subcategories;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<UnitsResponse> getUnitResponses() {
        return unitResponses;
    }

    public void setUnitResponses(List<UnitsResponse> unitResponses) {
        this.unitResponses = unitResponses;
    }

    public List<SubCategoriesResponse> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<SubCategoriesResponse> subcategories) {
        this.subcategories = subcategories;
    }
}
