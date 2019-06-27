package com.softmed.stockapp.Dom.responces;

import com.google.gson.annotations.SerializedName;
import com.softmed.stockapp.Dom.entities.Location;

import java.io.Serializable;
import java.util.List;

public class LoginResponse implements Serializable{

    @SerializedName("key")
    private String key;

    @SerializedName("user")
    private UserResponse user;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
