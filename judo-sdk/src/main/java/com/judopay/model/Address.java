package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An Address linked to a payment card, used when address verification is performed.
 */
@SuppressWarnings("unused")
public class Address implements Parcelable {

    private long countryCode;
    private String postCode;

    public long getCountryCode() {
        return countryCode;
    }

    public String getPostCode() {
        return postCode;
    }

    public static class Builder {

        private final Address address;

        public Builder() {
            this.address = new Address();
        }

        public Builder setCountryCode(long countryCode) {
            address.countryCode = countryCode;
            return this;
        }

        public Builder setPostCode(String postCode) {
            address.postCode = postCode;
            return this;
        }

        public Address build() {
            return address;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.countryCode);
        dest.writeString(this.postCode);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.countryCode = in.readLong();
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