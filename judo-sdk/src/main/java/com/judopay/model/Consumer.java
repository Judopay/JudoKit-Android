package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public final class Consumer implements Parcelable {

    private String consumerToken;
    private final String yourConsumerReference;

    public Consumer(final String consumerToken, final String yourConsumerReference) {
        this.consumerToken = consumerToken;
        this.yourConsumerReference = yourConsumerReference;
    }

    public Consumer(final String yourConsumerReference) {
        this.yourConsumerReference = yourConsumerReference;
    }

    private Consumer(final Parcel in) {
        this.consumerToken = in.readString();
        this.yourConsumerReference = in.readString();
    }

    public static final Creator<Consumer> CREATOR = new Creator<Consumer>() {
        public Consumer createFromParcel(final Parcel source) {
            return new Consumer(source);
        }

        public Consumer[] newArray(final int size) {
            return new Consumer[size];
        }
    };

    public String getConsumerToken() {
        return consumerToken;
    }

    public String getYourConsumerReference() {
        return yourConsumerReference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.consumerToken);
        dest.writeString(this.yourConsumerReference);
    }

}