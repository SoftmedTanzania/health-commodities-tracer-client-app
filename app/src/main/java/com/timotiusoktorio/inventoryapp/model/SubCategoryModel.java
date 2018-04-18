package com.timotiusoktorio.inventoryapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Coze on 2016-08-03.
 */

public class SubCategoryModel implements Parcelable {

    private long mId;
    private long mCategorySubCatogoryId;
    private String mName;
    private boolean isValid;

    public SubCategoryModel() {}

    public SubCategoryModel(long mId,long mCategorySubCatogoryId, String mName, boolean isValid) {
        this.mId = mId;
        this.mCategorySubCatogoryId = mCategorySubCatogoryId;
        this.mName = mName;
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

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public long getmCategorySubCatogoryId() {
        return mCategorySubCatogoryId;
    }

    public void setmCategorySubCatogoryId(long mCategorySubCatogoryId) {
        this.mCategorySubCatogoryId = mCategorySubCatogoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeLong(this.mCategorySubCatogoryId);
        dest.writeString(this.mName);
        dest.writeInt(this.isValid ?1:0);
    }

    protected SubCategoryModel(Parcel in) {
        this.mId = in.readLong();
        this.mCategorySubCatogoryId = in.readLong();
        this.mName = in.readString();
        this.isValid = in.readInt()==1?true:false;
    }

    public static final Creator<SubCategoryModel> CREATOR = new Creator<SubCategoryModel>() {
        @Override
        public SubCategoryModel createFromParcel(Parcel source) {
            return new SubCategoryModel(source);
        }

        @Override
        public SubCategoryModel[] newArray(int size) {
            return new SubCategoryModel[size];
        }
    };

}