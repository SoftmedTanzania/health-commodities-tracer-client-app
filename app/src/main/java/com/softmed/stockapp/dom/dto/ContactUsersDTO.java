package com.softmed.stockapp.dom.dto;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class ContactUsersDTO implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("password")
    private String password;

    @SerializedName("health_facility")
    private int health_facility;


    @SerializedName("health_facility_name")
    private String health_facility_name;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("middlename")
    private String middleName;

    @SerializedName("surname")
    private String surname;

    @SerializedName("username")
    private String username;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHealth_facility() {
        return health_facility;
    }

    public void setHealth_facility(int health_facility) {
        this.health_facility = health_facility;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHealth_facility_name() {
        return health_facility_name;
    }

    public void setHealth_facility_name(String health_facility_name) {
        this.health_facility_name = health_facility_name;
    }
}
