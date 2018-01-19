package com.junxu.androidaidllearn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Linxu on 2018-1-17.
 */

public class Writer implements Parcelable {
    private String name;

    public Writer() {
    }

    public Writer(Parcel in){
        this.name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        this.name = parcel.readString();
    }

    public static final Creator<Writer> CREATOR = new Creator<Writer>() {
        @Override
        public Writer createFromParcel(Parcel parcel) {
            return new Writer(parcel);
        }

        @Override
        public Writer[] newArray(int i) {
            return new Writer[i];
        }
    };
}
