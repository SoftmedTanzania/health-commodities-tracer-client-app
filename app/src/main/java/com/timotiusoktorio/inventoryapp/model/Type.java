package com.timotiusoktorio.inventoryapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Coze on 2016-08-03.
 */

public class Type implements Parcelable {

    private long mId;
    private long categorySubCategoryId;
    private String mName;
    private String mDescritption;
    private boolean isValid;

    public Type() {}

    public Type(long mId, long categorySubCategoryId, String mName, String mDescritption, boolean isValid) {
        this.mId = mId;
        this.categorySubCategoryId = categorySubCategoryId;
        this.mName = mName;
        this.mDescritption = mDescritption;
        this.isValid = isValid;
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

    public String getmDescritption() {
        return mDescritption;
    }

    public void setmDescritption(String mDescritption) {
        this.mDescritption = mDescritption;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public long getCategorySubCategoryId() {
        return categorySubCategoryId;
    }

    public void setCategorySubCategoryId(long categorySubCategoryId) {
        this.categorySubCategoryId = categorySubCategoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeLong(this.categorySubCategoryId);
        dest.writeString(this.mName);
        dest.writeString(this.mDescritption);
        dest.writeInt(this.isValid ?1:0);
    }

    protected Type(Parcel in) {
        this.mId = in.readLong();
        this.categorySubCategoryId = in.readLong();
        this.mName = in.readString();
        this.mDescritption = in.readString();
        this.isValid = in.readInt()==1?true:false;
    }

    public static final Creator<Type> CREATOR = new Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel source) {
            return new Type(source);
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };

}