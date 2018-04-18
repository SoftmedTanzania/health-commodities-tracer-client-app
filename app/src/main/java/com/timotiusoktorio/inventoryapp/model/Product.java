package com.timotiusoktorio.inventoryapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Coze on 2016-08-03.
 */

public class Product implements Parcelable {

    private long mId;
    private String mName;
    private long categorySubCategoryId;
    private long categoryTypeId;
    private long subCategoryTypeId;
    private long unitOfMeasureId;
    private String mPhotoPath;
    private String mSupplier;
    private double mPrice;
    private int mQuantity;

    public Product() {}

    public Product(long mId, String mName, long categorySubCategoryId, long categoryTypeId, long subCategoryTypeId, long unitOfMeasureId, String mPhotoPath, String mSupplier, double mPrice, int mQuantity) {
        this.mId = mId;
        this.mName = mName;
        this.categorySubCategoryId = categorySubCategoryId;
        this.categoryTypeId = categoryTypeId;
        this.subCategoryTypeId = subCategoryTypeId;
        this.unitOfMeasureId = unitOfMeasureId;
        this.mPhotoPath = mPhotoPath;
        this.mSupplier = mSupplier;
        this.mPrice = mPrice;
        this.mQuantity = mQuantity;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public long getCategorySubCategoryId() {
        return categorySubCategoryId;
    }

    public void setCategorySubCategoryId(long categorySubCategoryId) {
        this.categorySubCategoryId = categorySubCategoryId;
    }

    public long getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(long categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

    public long getSubCategoryTypeId() {
        return subCategoryTypeId;
    }

    public void setSubCategoryTypeId(long subCategoryTypeId) {
        this.subCategoryTypeId = subCategoryTypeId;
    }

    public long getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(long unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public String getmPhotoPath() {
        return mPhotoPath;
    }

    public void setmPhotoPath(String mPhotoPath) {
        this.mPhotoPath = mPhotoPath;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmSupplier() {
        return mSupplier;
    }

    public void setmSupplier(String mSupplier) {
        this.mSupplier = mSupplier;
    }

    @Override
    public String toString() {
        return "Product{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", categorySubCategoryId='" + categorySubCategoryId + '\'' +
                ", mSupplier='" + mSupplier + '\'' +
                ", mPhotoPath='" + mPhotoPath + '\'' +
                ", mPrice=" + mPrice +
                ", mQuantity=" + mQuantity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeLong(this.categorySubCategoryId);
        dest.writeLong(this.categoryTypeId);
        dest.writeLong(this.subCategoryTypeId);
        dest.writeLong(this.unitOfMeasureId);
        dest.writeString(this.mSupplier);
        dest.writeString(this.mPhotoPath);
        dest.writeDouble(this.mPrice);
        dest.writeInt(this.mQuantity);
    }

    protected Product(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.categorySubCategoryId = in.readLong();
        this.categoryTypeId = in.readLong();
        this.subCategoryTypeId = in.readLong();
        this.unitOfMeasureId = in.readLong();
        this.mSupplier = in.readString();
        this.mPhotoPath = in.readString();
        this.mPrice = in.readDouble();
        this.mQuantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

}