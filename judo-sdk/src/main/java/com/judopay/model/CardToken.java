package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.judopay.arch.TextUtil;

/**
 * The tokenized card data from registering a card, allowing for token payments and token pre-auths
 * to be performed.
 */
public class CardToken implements Parcelable {

    public static final Creator<CardToken> CREATOR = new Creator<CardToken>() {
        public CardToken createFromParcel(Parcel source) {
            return new CardToken(source);
        }

        public CardToken[] newArray(int size) {
            return new CardToken[size];
        }
    };

    private String endDate;
    @SerializedName("cardLastfour")
    private String lastFour;
    @SerializedName("cardToken")
    private String token;
    @SerializedName("cardType")
    private int type;
    @SerializedName("cardScheme")
    private String scheme;
    @SerializedName("cardFunding")
    private String funding;
    @SerializedName("cardCategory")
    private String category;
    @SerializedName("cardCountry")
    private String country;
    private String bank;

    public CardToken() {
    }

    public CardToken(String endDate, String lastFour, String token, int type, String scheme, String funding, String category, String country, String bank) {
        this.endDate = endDate;
        this.lastFour = lastFour;
        this.token = token;
        this.type = type;
        this.scheme = scheme;
        this.funding = funding;
        this.category = category;
        this.country = country;
        this.bank = bank;
    }

    private CardToken(Parcel in) {
        endDate = in.readString();
        lastFour = in.readString();
        token = in.readString();
        type = in.readInt();
        scheme = in.readString();
        funding = in.readString();
        category = in.readString();
        country = in.readString();
        bank = in.readString();
    }

    public String getEndDate() {
        return endDate;
    }

    public String getFormattedEndDate() {
        if (TextUtil.isEmpty(endDate) || endDate.length() != 4) {
            return "";
        } else {
            return String.format("%s/%s", endDate.substring(0, 2), endDate.substring(2, 4));
        }
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getToken() {
        return token;
    }

    public int getType() {
        return type;
    }

    public String getScheme() {
        return scheme;
    }

    public String getFunding() {
        return funding;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public String getBank() {
        return bank;
    }

    @Override
    public String toString() {
        return "CardToken{" +
                "endDate='" + endDate + '\'' +
                ", lastFour='" + lastFour + '\'' +
                ", token='" + token + '\'' +
                ", type=" + type +
                ", scheme='" + scheme + '\'' +
                ", funding='" + funding + '\'' +
                ", category='" + category + '\'' +
                ", country='" + country + '\'' +
                ", bank='" + bank + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(endDate);
        dest.writeString(lastFour);
        dest.writeString(token);
        dest.writeInt(type);
        dest.writeString(scheme);
        dest.writeString(funding);
        dest.writeString(category);
        dest.writeString(country);
        dest.writeString(bank);
    }

    public boolean isExpired() {
        CardDate cardDate = new CardDate(endDate);
        return cardDate.isBeforeToday() || !cardDate.isInsideAllowedDateRange();
    }
}
