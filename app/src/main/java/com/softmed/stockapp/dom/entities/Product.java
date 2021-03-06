package com.softmed.stockapp.dom.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class Product implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("health_commodity_category")
    private int category_id;

    @SerializedName("unit")
    private int unit_id;

    @SerializedName("posting_frequency")
    private int posting_frequency;

    @SerializedName("health_commodity_name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("track_number_of_patients")
    private boolean track_number_of_patients;

    @SerializedName("track_wastage")
    private boolean track_wastage;

    @SerializedName("track_quantity_expired")
    private boolean track_quantity_expired;

    @SerializedName("has_patients")
    private boolean track_has_patients;

    @SerializedName("is_active")
    private boolean is_active;

    @SerializedName("local_image_path")
    private String local_image_path;

    @SerializedName("status")
    private int status = 1;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getUnitId() {
        return unit_id;
    }

    public void setUnitId(int unit_id) {
        this.unit_id = unit_id;
    }

    public boolean isTrackNumberOfPatients() {
        return track_number_of_patients;
    }

    public void setTrackNumberOfPatients(boolean track_number_of_patients) {
        this.track_number_of_patients = track_number_of_patients;
    }

    public boolean isTrackWastage() {
        return track_wastage;
    }

    public void setTrack_wastage(boolean track_wastage) {
        this.track_wastage = track_wastage;
    }

    public boolean isTrackQuantityExpired() {
        return track_quantity_expired;
    }

    public void setTrack_quantity_expired(boolean track_quantity_expired) {
        this.track_quantity_expired = track_quantity_expired;
    }

    public String getLocalImagePath() {
        return local_image_path;
    }

    public void setLocalImagePath(String local_image_path) {
        this.local_image_path = local_image_path;
    }

    public int getPostingFrequency() {
        return posting_frequency;
    }

    public void setPostingFrequency(int posting_frequency) {
        this.posting_frequency = posting_frequency;
    }

    public boolean isIsActive() {
        return is_active;
    }

    public void setIsActive(boolean is_active) {
        this.is_active = is_active;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public int getPosting_frequency() {
        return posting_frequency;
    }

    public void setPosting_frequency(int posting_frequency) {
        this.posting_frequency = posting_frequency;
    }

    public boolean isTrack_number_of_patients() {
        return track_number_of_patients;
    }

    public void setTrack_number_of_patients(boolean track_number_of_patients) {
        this.track_number_of_patients = track_number_of_patients;
    }

    public boolean isTrack_wastage() {
        return track_wastage;
    }

    public boolean isTrack_quantity_expired() {
        return track_quantity_expired;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getLocal_image_path() {
        return local_image_path;
    }

    public void setLocal_image_path(String local_image_path) {
        this.local_image_path = local_image_path;
    }

    public boolean isTrack_has_patients() {
        return track_has_patients;
    }

    public void setTrack_has_patients(boolean track_has_patients) {
        this.track_has_patients = track_has_patients;
    }
}
