package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.judopay.arch.TextUtil;

/**
 * The tokenized card data from registering a card, allowing for token payments and token pre-auths
 * to be performed.
 */
@SuppressWarnings("SameParameterValue")
public class CardToken implements Parcelable {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private int type;

    public CardToken() {}

    public CardToken(String endDate, String lastFour, String token, int type) {
        this.endDate = endDate;
        this.lastFour = lastFour;
        this.token = token;
        this.type = type;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getFormattedEndDate() {
        if(TextUtil.isEmpty(endDate) || endDate.length() != 4) {
            return "";
        } else {
            return String.format("%s/%s", endDate.substring(0, 2), endDate.substring(2, 4));
        }
    }

    public String getToken() {
        return token;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CardToken{" +
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
        dest.writeInt(this.type);
    }

    private CardToken(Parcel in) {
        this.endDate = in.readString();
        this.lastFour = in.readString();
        this.token = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<CardToken> CREATOR = new Creator<CardToken>() {
        public CardToken createFromParcel(Parcel source) {
            return new CardToken(source);
        }

        public CardToken[] newArray(int size) {
            return new CardToken[size];
        }
    };

    public boolean isExpired() {
        CardDate cardDate = new CardDate(endDate);
        return cardDate.isBeforeToday() || !cardDate.isInsideAllowedDateRange();
    }

}