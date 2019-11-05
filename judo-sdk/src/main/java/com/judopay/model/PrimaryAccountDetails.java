package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public final class PrimaryAccountDetails implements Parcelable {
    private String name;
    private String accountNumber;
    private String dateOfBirth;
    private String postCode;

    public PrimaryAccountDetails(String name, String accountNumber, String dateOfBirth, String postCode) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.dateOfBirth = dateOfBirth;
        this.postCode = postCode;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPostCode() {
        return postCode;
    }

    public static class Builder {
        private String name;
        private String accountNumber;
        private String dateOfBirth;
        private String postCode;

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setAccountNumber(final String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder setDateOfBirth(final String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setPostCode(final String postCode) {
            this.postCode = postCode;
            return this;
        }

        public PrimaryAccountDetails build() {
            return new PrimaryAccountDetails(name, accountNumber, dateOfBirth, postCode);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.name);
        dest.writeString(this.accountNumber);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.postCode);
    }

    protected PrimaryAccountDetails(final Parcel in) {
        this.name = in.readString();
        this.accountNumber = in.readString();
        this.dateOfBirth = in.readString();
        this.postCode = in.readString();
    }

    public static final Parcelable.Creator<PrimaryAccountDetails> CREATOR = new Parcelable.Creator<PrimaryAccountDetails>() {
        @Override
        public PrimaryAccountDetails createFromParcel(final Parcel source) {
            return new PrimaryAccountDetails(source);
        }

        @Override
        public PrimaryAccountDetails[] newArray(final int size) {
            return new PrimaryAccountDetails[size];
        }
    };
}
