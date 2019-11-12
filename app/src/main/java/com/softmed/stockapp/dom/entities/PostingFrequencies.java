package com.softmed.stockapp.dom.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class PostingFrequencies implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("frequency_description")
    private String frequency_description;

    @SerializedName("number_of_days")
    private int number_of_days;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrequencyDescription() {
        return frequency_description;
    }

    public void setFrequencyDescription(String frequency_description) {
        this.frequency_description = frequency_description;
    }

    public int getNumber_of_days() {
        return number_of_days;
    }

    public void setNumber_of_days(int number_of_days) {
        this.number_of_days = number_of_days;
    }

    public String getFrequency_description() {
        return frequency_description;
    }

    public void setFrequency_description(String frequency_description) {
        this.frequency_description = frequency_description;
    }
}
