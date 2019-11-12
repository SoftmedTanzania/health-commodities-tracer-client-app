package com.softmed.stockapp.dom.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class UsersInfo implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("password")
    private String password;

    @SerializedName("phone_no")
    private int phone_no;

    @SerializedName("health_facility")
    private int health_facility;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("middlename")
    private String middleName;

    @SerializedName("surname")
    private String surname;

    @SerializedName("username")
    private String username;

    @SerializedName("assigned_location_type")
    private String assignedLocationType;

    @SerializedName("districtId")
    private int districtId;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPhoneNo() {
        return phone_no;
    }

    public void setPhoneNo(int phone_no) {
        this.phone_no = phone_no;
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

    public String getAssignedLocationType() {
        return assignedLocationType;
    }

    public void setAssignedLocationType(String assignedLocationType) {
        this.assignedLocationType = assignedLocationType;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(int phone_no) {
        this.phone_no = phone_no;
    }
}
