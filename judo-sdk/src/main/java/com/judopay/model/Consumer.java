package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class Consumer implements Parcelable {

    private String consumerToken;
    private final String yourConsumerReference;

    public Consumer(String consumerToken, String yourConsumerReference) {
        this.consumerToken = consumerToken;
        this.yourConsumerReference = yourConsumerReference;
    }

    public Consumer(String yourConsumerReference) {
        this.yourConsumerReference = yourConsumerReference;
    }

    private Consumer(Parcel in) {
        this.consumerToken = in.readString();
        this.yourConsumerReference = in.readString();
    }

    public static final Creator<Consumer> CREATOR = new Creator<Consumer>() {
        public Consumer createFromParcel(Parcel source) {
            return new Consumer(source);
        }

        public Consumer[] newArray(int size) {
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.consumerToken);
        dest.writeString(this.yourConsumerReference);
    }

}