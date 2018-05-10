package com.timotiusoktorio.inventoryapp.dom.responces;

import com.google.gson.annotations.SerializedName;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.Unit;

import java.io.Serializable;
import java.util.List;


public class UnitsResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    List<Unit> units;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }
}
