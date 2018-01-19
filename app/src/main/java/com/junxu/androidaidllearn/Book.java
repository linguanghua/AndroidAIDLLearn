package com.junxu.androidaidllearn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Linxu on 2018-1-16.
 */

public class Book implements Parcelable{
    private String name;
    private Integer price;

    public Book() {
    }

    public Book(Parcel in){
        this.name = in.readString();
        this.price = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int i) {
            return new Book[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(price);
    }

    public void readFromParcel(Parcel parcel){
        this.name = parcel.readString();
        this.price = parcel.readInt();
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
