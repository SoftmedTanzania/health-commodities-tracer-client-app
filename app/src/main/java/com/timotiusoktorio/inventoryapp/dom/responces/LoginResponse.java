package com.timotiusoktorio.inventoryapp.dom.responces;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;

import java.io.Serializable;
import java.util.List;

public class LoginResponse implements Serializable{

    @SerializedName("id")
    private int id;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("middlename")
    private String middleName;

    @SerializedName("surname")
    private String surname;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("levels")
    List<LevelResponse> levelResponses;

    @SerializedName("locations")
    List<LocationResponse> locationResponses;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<LevelResponse> getLevelResponses() {
        return levelResponses;
    }

    public void setLevelResponses(List<LevelResponse> levelResponses) {
        this.levelResponses = levelResponses;
    }

    public List<LocationResponse> getLocationResponses() {
        return locationResponses;
    }

    public void setLocationResponses(List<LocationResponse> locationResponses) {
        this.locationResponses = locationResponses;
    }
}
