package com.judopay.payment;

import android.os.Parcel;
import android.os.Parcelable;

public class Risks implements Parcelable {

    private String postCodeCheck;

    public Risks() { }

    public String getPostCodeCheck() {
        return postCodeCheck;
    }

    @Override
    public String toString() {
        return "Risks{" +
                "postCodeCheck='" + postCodeCheck + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postCodeCheck);
    }

    private Risks(Parcel in) {
        this.postCodeCheck = in.readString();
    }

    public static final Parcelable.Creator<Risks> CREATOR = new Parcelable.Creator<Risks>() {
        public Risks createFromParcel(Parcel source) {
            return new Risks(source);
        }

        public Risks[] newArray(int size) {
            return new Risks[size];
        }
    };

}