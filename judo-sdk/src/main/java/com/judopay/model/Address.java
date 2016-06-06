package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An Address linked to a payment card, used when address verification is performed.
 */
@SuppressWarnings("unused")
public class Address implements Parcelable {

    private String line1;
    private String line2;
    private String line3;
    private String town;
    private Integer countryCode;
    private String postCode;

    public Address(String line1, String line2, String line3, String town, String postCode, Integer countryCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.town = town;
        this.postCode = postCode;
        this.countryCode = countryCode;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getLine3() {
        return line3;
    }

    public String getTown() {
        return town;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public String getPostCode() {
        return postCode;
    }

    public static class Builder {

        private String line1;
        private String line2;
        private String line3;
        private String postCode;
        private String town;
        private Integer countryCode;

        public Builder setLine1(String line1) {
            this.line1 = line1;
            return this;
        }

        public Builder setLine2(String line2) {
            this.line2 = line2;
            return this;
        }

        public Builder setLine3(String line3) {
            this.line3 = line3;
            return this;
        }

        public Builder setTown(String town) {
            this.town = town;
            return this;
        }

        public Builder setCountryCode(Integer countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public Builder setPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Address build() {
            return new Address(line1, line2, line3, postCode, town, countryCode);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.line1);
        dest.writeString(this.line2);
        dest.writeString(this.line3);
        dest.writeString(this.town);
        dest.writeValue(this.countryCode);
        dest.writeString(this.postCode);
    }

    protected Address(Parcel in) {
        this.line1 = in.readString();
        this.line2 = in.readString();
        this.line3 = in.readString();
        this.town = in.readString();
        this.countryCode = in.readInt();
        this.postCode = in.readString();
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}