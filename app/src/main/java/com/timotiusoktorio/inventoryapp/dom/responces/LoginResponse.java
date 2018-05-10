package com.timotiusoktorio.inventoryapp.dom.responces;

import com.google.gson.annotations.SerializedName;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;

import java.io.Serializable;
import java.util.List;

public class LoginResponse implements Serializable{

    @SerializedName("data")
    private List<UsersInfo> usersInfoList;


    @SerializedName("success")
    private boolean success;

    public List<UsersInfo> getUsersInfoList() {
        return usersInfoList;
    }

    public void setUsersInfoList(List<UsersInfo> usersInfoList) {
        this.usersInfoList = usersInfoList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
