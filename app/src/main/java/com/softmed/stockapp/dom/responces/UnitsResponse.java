package com.softmed.stockapp.dom.responces;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class UnitsResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("abbreviation")
    private String name;

    @SerializedName("unit_description")
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
