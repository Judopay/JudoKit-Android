package com.judopay.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.Consumer;

public class Payment implements Parcelable {

    private float amount;
    private long judoId;
    private String currency;
    private String paymentRef;
    private Consumer consumer;

    public float getAmount() {
        return amount;
    }

    public long getJudoId() {
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
        dest.writeFloat(this.amount);
        dest.writeLong(this.judoId);
        dest.writeString(this.currency);
        dest.writeString(this.paymentRef);
        dest.writeParcelable(this.consumer, flags);
    }

    private Payment() { }

    private Payment(Parcel in) {
        this.amount = in.readFloat();
        this.judoId = in.readLong();
        this.currency = in.readString();
        this.paymentRef = in.readString();
        this.consumer = in.readParcelable(Consumer.class.getClassLoader());
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
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

        public Builder setAmount(float amount) {
            payment.amount = amount;
            return this;
        }

        public Builder setJudoId(long judoId) {
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
            if(payment.amount == 0) {
                throw new IllegalArgumentException("");
            }

            if(payment.judoId == 0) {
                throw new IllegalArgumentException("");
            }

            if(payment.currency == null || payment.currency.length() == 0) {
                throw new IllegalArgumentException("");
            }

            if(payment.paymentRef == null || payment.paymentRef.length() == 0) {
                throw new IllegalArgumentException("");
            }

            if(payment.consumer == null) {
                throw new IllegalArgumentException("");
            }

            return payment;
        }

    }
}
