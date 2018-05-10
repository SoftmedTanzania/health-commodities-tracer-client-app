package com.timotiusoktorio.inventoryapp.dom.responces;

import com.google.gson.annotations.SerializedName;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;

import java.io.Serializable;
import java.util.List;


public class CategoriesResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    List<Category> categories;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
