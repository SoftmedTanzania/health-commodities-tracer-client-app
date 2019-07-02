package com.softmed.stockapp.Dom.responces;

import com.google.gson.annotations.SerializedName;
import com.softmed.stockapp.Dom.entities.Location;

import java.io.Serializable;
import java.util.List;

public class ProfileResponse implements Serializable{

    @SerializedName("user")
    private int userId;

    @SerializedName("phone_no")
    private String phoneNo;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("location")
    private int healthFacility;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(int healthFacility) {
        this.healthFacility = healthFacility;
    }
}
