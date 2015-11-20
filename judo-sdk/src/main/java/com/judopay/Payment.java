package com.judopay;

import android.os.Parcel;
import android.os.Parcelable;

public class Payment implements Parcelable {

    private String amount;
    private String judoId;
    private String currency;
    private String paymentRef;
    private Consumer consumer;

    public Payment() { }

    public String getAmount() {
        return amount;
    }

    public String getJudoId() {
        return judoId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public Consumer getConsumer() {
        return consumer;
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
        dest.writeString(this.paymentRef);
        dest.writeParcelable(this.consumer, 0);
    }

    private Payment(Parcel in) {
        this.amount = in.readString();
        this.judoId = in.readString();
        this.currency = in.readString();
        this.paymentRef = in.readString();
        this.consumer = in.readParcelable(Consumer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Payment> CREATOR = new Parcelable.Creator<Payment>() {
        public Payment createFromParcel(Parcel source) {
            return new Payment(source);
        }

        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    public static class Builder {

        private final Payment payment;

        public Builder() {
            payment = new Payment();
        }

        public Builder setAmount(String amount) {
            payment.amount = amount;
            return this;
        }

        public Builder setJudoId(String judoId) {
            payment.judoId = judoId;
            return this;
        }

        public Builder setCurrency(String currency) {
            payment.currency = currency;
            return this;
        }

        public Builder setPaymentRef(String paymentRef) {
            payment.paymentRef = paymentRef;
            return this;
        }

        public Builder setConsumer(Consumer consumer) {
            payment.consumer = consumer;
            return this;
        }

        public Payment build() {
            if (payment.amount == null || payment.amount.length() == 0) {
                throw new IllegalArgumentException("Payment.amount must be supplied");
            }

            if (payment.judoId == null || payment.judoId.length() == 0) {
                throw new IllegalArgumentException("Payment.judoId must be supplied");
            }

            if (payment.currency == null || payment.currency.length() == 0) {
                throw new IllegalArgumentException("Payment.currency must be supplied");
            }

            if (payment.paymentRef == null || payment.paymentRef.length() == 0) {
                throw new IllegalArgumentException("Payment.paymentRef must be supplied");
            }

            if (payment.consumer == null) {
                throw new IllegalArgumentException("Payment.consumer must be supplied");
            }

            return payment;
        }
    }
}
