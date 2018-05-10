package com.timotiusoktorio.inventoryapp.dom.responces;

import com.google.gson.annotations.SerializedName;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;

import java.io.Serializable;
import java.util.List;


public class SubCategoriesResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    List<SubCategory> subCategories;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
