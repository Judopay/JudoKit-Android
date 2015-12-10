package com.judopay.payment.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.model.CardToken;

public class PaymentFormOptions implements Parcelable {

    private String buttonLabel;
    private CardToken cardToken;

    private PaymentFormOptions() { }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public CardToken getCardToken() {
        return cardToken;
    }

    public static class Builder {
        private String buttonLabel;
        private CardToken cardToken;

        public Builder setButtonLabel(String buttonLabel) {
            this.buttonLabel = buttonLabel;
            return this;
        }

        public Builder setCardToken(CardToken cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public PaymentFormOptions build() {
            PaymentFormOptions paymentFormOptions = new PaymentFormOptions();

            paymentFormOptions.buttonLabel = buttonLabel;
            paymentFormOptions.cardToken = cardToken;

            return paymentFormOptions;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buttonLabel);
        dest.writeParcelable(this.cardToken, 0);
    }

    private PaymentFormOptions(Parcel in) {
        this.buttonLabel = in.readString();
        this.cardToken = in.readParcelable(CardToken.class.getClassLoader());
    }

    public static final Parcelable.Creator<PaymentFormOptions> CREATOR = new Parcelable.Creator<PaymentFormOptions>() {
        public PaymentFormOptions createFromParcel(Parcel source) {
            return new PaymentFormOptions(source);
        }

        public PaymentFormOptions[] newArray(int size) {
            return new PaymentFormOptions[size];
        }
    };

}