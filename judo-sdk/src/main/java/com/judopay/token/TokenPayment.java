package com.judopay.token;

import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.Consumer;
import com.judopay.customer.CardToken;

import java.util.HashMap;

public class TokenPayment implements Parcelable {

    private CardToken cardToken;
    private Consumer consumer;

    private String judoId;
    private String currency;
    private String amount;
    private String paymentReference;
    private HashMap<String, String> yourMetaData;

    public String getJudoId() {
        return judoId;
    }

    public CardToken getCardToken() {
        return cardToken;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public HashMap<String, String> getYourMetaData() {
        return yourMetaData;
    }

    public static class Builder {

        private TokenPayment tokenPayment;

        public Builder() {
            this.tokenPayment = new TokenPayment();
        }

        public Builder setCardToken(CardToken cardToken) {
            this.tokenPayment.cardToken = cardToken;
            return this;
        }

        public Builder setConsumer(Consumer consumer) {
            this.tokenPayment.consumer = consumer;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.tokenPayment.currency = currency;
            return this;
        }

        public Builder setAmount(String amount) {
            this.tokenPayment.amount = amount;
            return this;
        }

        public Builder setPaymentReference(String paymentReference) {
            this.tokenPayment.paymentReference = paymentReference;
            return this;
        }

        public Builder setJudoId(String judoId) {
            this.tokenPayment.judoId = judoId;
            return this;
        }

        public Builder setYourMetaData(HashMap<String, String> yourMetaData) {
            this.tokenPayment.yourMetaData = yourMetaData;
            return this;
        }

        public TokenPayment build() {
            if(tokenPayment.judoId == null || tokenPayment.judoId.length() == 0) {
                throw new IllegalArgumentException("TokenPayment.judoId must be supplied");
            }

            if (tokenPayment.amount == null || tokenPayment.amount.length() == 0) {
                throw new IllegalArgumentException("TokenPayment.amount must be supplied");
            }

            if (tokenPayment.currency == null || tokenPayment.currency.length() == 0) {
                throw new IllegalArgumentException("TokenPayment.currency must be supplied");
            }

            if (tokenPayment.paymentReference == null || tokenPayment.paymentReference.length() == 0) {
                throw new IllegalArgumentException("TokenPayment.paymentReference must be supplied");
            }

            if (tokenPayment.consumer == null) {
                throw new IllegalArgumentException("TokenPayment.consumer must be supplied");
            }

            if (tokenPayment.cardToken == null) {
                throw new IllegalArgumentException("TokenPayment.cardToken must be supplied");
            }

            return tokenPayment;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.cardToken, 0);
        dest.writeParcelable(this.consumer, 0);
        dest.writeString(this.judoId);
        dest.writeString(this.currency);
        dest.writeString(this.amount);
        dest.writeString(this.paymentReference);
        dest.writeSerializable(this.yourMetaData);
    }

    public TokenPayment() { }

    private TokenPayment(Parcel in) {
        this.cardToken = in.readParcelable(CardToken.class.getClassLoader());
        this.consumer = in.readParcelable(Consumer.class.getClassLoader());
        this.judoId = in.readString();
        this.currency = in.readString();
        this.amount = in.readString();
        this.paymentReference = in.readString();

        //noinspection unchecked
        this.yourMetaData = (HashMap<String, String>) in.readSerializable();
    }

    public static final Parcelable.Creator<TokenPayment> CREATOR = new Parcelable.Creator<TokenPayment>() {
        public TokenPayment createFromParcel(Parcel source) {
            return new TokenPayment(source);
        }

        public TokenPayment[] newArray(int size) {
            return new TokenPayment[size];
        }
    };

}