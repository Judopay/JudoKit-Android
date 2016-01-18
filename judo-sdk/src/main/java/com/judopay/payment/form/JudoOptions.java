package com.judopay.payment.form;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.model.CardToken;

import java.util.HashMap;
import java.util.Map;

public class JudoOptions implements Parcelable {

    private String amount;
    private String judoId;
    private String currency;
    private String consumerRef;
    private Bundle metaData;
    private String cardNumber;
    private String expiryDate;
    private String cv2;
    private String buttonLabel;
    private CardToken cardToken;

    private JudoOptions() {
    }

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

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCv2() {
        return cv2;
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
        private String expiryDate;
        private String cv2;
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

        public Builder setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.cv2 = cv2;
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
            options.expiryDate = expiryDate;
            options.cv2 = cv2;
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
        dest.writeString(this.expiryDate);
        dest.writeString(this.cv2);
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
        this.expiryDate = in.readString();
        this.cv2 = in.readString();
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