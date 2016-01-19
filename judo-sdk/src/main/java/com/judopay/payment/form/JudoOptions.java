package com.judopay.payment.form;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.model.CardToken;

import java.util.HashMap;
import java.util.Map;

public class JudoOptions implements Parcelable {

    private String judoId;
    private String amount;
    private String currency;
    private String consumerRef;
    private Bundle metaData;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    private String buttonLabel;
    private CardToken cardToken;

    private JudoOptions() { }

    public String getAmount() {
        return amount;
    }

    public String getJudoId() {
        return judoId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getConsumerRef() {
        return consumerRef;
    }

    public Bundle getMetaData() {
        return metaData;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public CardToken getCardToken() {
        return cardToken;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String getCvv() {
        return cvv;
    }

    public Map<String, String> getMetaDataMap() {
        Map<String, String> map = new HashMap<>();

        if (metaData != null) {
            for (String key : metaData.keySet()) {
                String value = metaData.getString(key);
                map.put(key, value);
            }
        }
        return map;
    }

    public static class Builder {

        private String buttonLabel;
        private CardToken cardToken;
        private String cardNumber;
        private String expiryMonth;
        private String expiryYear;
        private String cvv;
        private String amount;
        private String judoId;
        private String currency;
        private String consumerRef;
        private Bundle metaData;

        public Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setJudoId(String judoId) {
            this.judoId = judoId;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setConsumerRef(String consumerRef) {
            this.consumerRef = consumerRef;
            return this;
        }

        public Builder setMetaData(Bundle metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setExpiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }

        public Builder setExpiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }

        public Builder setCvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public Builder setButtonLabel(String buttonLabel) {
            this.buttonLabel = buttonLabel;
            return this;
        }

        public Builder setCardToken(CardToken cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public JudoOptions build() {
            JudoOptions options = new JudoOptions();

            options.buttonLabel = buttonLabel;
            options.cardToken = cardToken;
            options.cardNumber = cardNumber;
            options.expiryMonth = expiryMonth;
            options.expiryYear = expiryYear;
            options.cvv = cvv;
            options.amount = amount;
            options.judoId = judoId;
            options.currency = currency;
            options.consumerRef = consumerRef;
            options.metaData = metaData;

            return options;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.amount);
        dest.writeString(this.judoId);
        dest.writeString(this.currency);
        dest.writeString(this.consumerRef);
        dest.writeBundle(metaData);
        dest.writeString(this.cardNumber);
        dest.writeString(this.expiryMonth);
        dest.writeString(this.expiryYear);
        dest.writeString(this.cvv);
        dest.writeString(this.buttonLabel);
        dest.writeParcelable(this.cardToken, 0);
    }

    protected JudoOptions(Parcel in) {
        this.amount = in.readString();
        this.judoId = in.readString();
        this.currency = in.readString();
        this.consumerRef = in.readString();
        metaData = in.readBundle();
        this.cardNumber = in.readString();
        this.expiryMonth = in.readString();
        this.expiryYear = in.readString();
        this.cvv = in.readString();
        this.buttonLabel = in.readString();
        this.cardToken = in.readParcelable(CardToken.class.getClassLoader());
    }

    public static final Parcelable.Creator<JudoOptions> CREATOR = new Parcelable.Creator<JudoOptions>() {
        public JudoOptions createFromParcel(Parcel source) {
            return new JudoOptions(source);
        }

        public JudoOptions[] newArray(int size) {
            return new JudoOptions[size];
        }
    };

}