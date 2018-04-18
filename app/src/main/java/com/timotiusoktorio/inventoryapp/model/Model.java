package com.timotiusoktorio.inventoryapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Coze on 2016-08-03.
 */

public class Model implements Parcelable {

    private long mId;
    private String mName;
    private boolean isValid;

    public Model() {}

    public Model(long mId, String mName, boolean isValid) {
        this.mId = mId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeInt(this.isValid ?1:0);
    }

    protected Model(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.isValid = in.readInt()==1?true:false;
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel source) {
            return new Model(source);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

}