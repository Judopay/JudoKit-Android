package com.judopay.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CardSummary implements Parcelable {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private long type;

    public CardSummary() { }

    public String getLastFour() {
        return lastFour;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getToken() {
        return token;
    }

    public long getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CardSummary{" +
                "endDate='" + endDate + '\'' +
                ", lastFour='" + lastFour + '\'' +
                ", token='" + token + '\'' +
                ", type=" + type +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.endDate);
        dest.writeString(this.lastFour);
        dest.writeString(this.token);
        dest.writeLong(this.type);
    }

    private CardSummary(Parcel in) {
        this.endDate = in.readString();
        this.lastFour = in.readString();
        this.token = in.readString();
        this.type = in.readLong();
    }

    public static final Parcelable.Creator<CardSummary> CREATOR = new Parcelable.Creator<CardSummary>() {
        public CardSummary createFromParcel(Parcel source) {
            return new CardSummary(source);
        }

        public CardSummary[] newArray(int size) {
            return new CardSummary[size];
        }
    };

}