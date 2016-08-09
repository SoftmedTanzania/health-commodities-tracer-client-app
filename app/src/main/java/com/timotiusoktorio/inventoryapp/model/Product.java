package com.timotiusoktorio.inventoryapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Timotius on 2016-08-03.
 */

public class Product implements Parcelable {

    private long mId;
    private String mName;
    private String mCode;
    private String mSupplier;
    private String mSupplierEmail;
    private String mPhotoPath;
    private double mPrice;
    private int mQuantity;

    public Product() {}

    public Product(long id, String name, String photoPath, double price, int quantity) {
        mId = id;
        mName = name;
        mPhotoPath = photoPath;
        mPrice = price;
        mQuantity = quantity;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getSupplier() {
        return mSupplier;
    }

    public void setSupplier(String supplier) {
        mSupplier = supplier;
    }

    public String getSupplierEmail() {
        return mSupplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        mSupplierEmail = supplierEmail;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mCode='" + mCode + '\'' +
                ", mSupplier='" + mSupplier + '\'' +
                ", mSupplierEmail='" + mSupplierEmail + '\'' +
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
        dest.writeString(this.mCode);
        dest.writeString(this.mSupplier);
        dest.writeString(this.mSupplierEmail);
        dest.writeString(this.mPhotoPath);
        dest.writeDouble(this.mPrice);
        dest.writeInt(this.mQuantity);
    }

    protected Product(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mCode = in.readString();
        this.mSupplier = in.readString();
        this.mSupplierEmail = in.readString();
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