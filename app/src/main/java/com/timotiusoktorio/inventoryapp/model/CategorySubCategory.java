package com.timotiusoktorio.inventoryapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Coze on 2016-08-03.
 */

public class CategorySubCategory implements Parcelable {

    private long mId;
    private long CategoryId;
    private long SubCategoryId;

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public long getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(long categoryId) {
        CategoryId = categoryId;
    }

    public long getSubCategoryId() {
        return SubCategoryId;
    }

    public void setSubCategoryId(long subCategoryId) {
        SubCategoryId = subCategoryId;
    }

    public CategorySubCategory() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeLong(this.CategoryId);
        dest.writeLong(this.SubCategoryId);
    }

    protected CategorySubCategory(Parcel in) {
        this.mId = in.readLong();
        this.CategoryId = in.readLong();
        this.SubCategoryId = in.readLong();
    }

    public static final Creator<CategorySubCategory> CREATOR = new Creator<CategorySubCategory>() {
        @Override
        public CategorySubCategory createFromParcel(Parcel source) {
            return new CategorySubCategory(source);
        }

        @Override
        public CategorySubCategory[] newArray(int size) {
            return new CategorySubCategory[size];
        }
    };

}