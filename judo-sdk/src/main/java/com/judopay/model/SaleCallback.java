package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SaleCallback implements Parcelable {
    private String orderId;
    private String paymentMethod;
    private String status;
    private String merchantPaymentReference;
    private String currency;
    private String amount;
    private String merchantConsumerReference;
    private String siteId;

    public SaleCallback(SaleResponse saleResponse, String paymentReference) {
        this.orderId = saleResponse.getOrderId();
        this.paymentMethod = saleResponse.getPaymentMethod();
        this.status = saleResponse.getStatus();
        this.merchantPaymentReference = saleResponse.getMerchantPaymentReference();
        this.currency = saleResponse.getCurrency();
        this.amount = saleResponse.getAmount();
        this.merchantConsumerReference = paymentReference;
        this.siteId = saleResponse.getSiteId();
    }

    protected SaleCallback(Parcel in) {
        orderId = in.readString();
        paymentMethod = in.readString();
        status = in.readString();
        merchantPaymentReference = in.readString();
        currency = in.readString();
        amount = in.readString();
        merchantConsumerReference = in.readString();
        siteId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(paymentMethod);
        dest.writeString(status);
        dest.writeString(merchantPaymentReference);
        dest.writeString(currency);
        dest.writeString(amount);
        dest.writeString(merchantConsumerReference);
        dest.writeString(siteId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SaleCallback> CREATOR = new Creator<SaleCallback>() {
        @Override
        public SaleCallback createFromParcel(Parcel in) {
            return new SaleCallback(in);
        }

        @Override
        public SaleCallback[] newArray(int size) {
            return new SaleCallback[size];
        }
    };
}
