package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SaleStatusResponse implements Parcelable {
    private OrderDetails orderDetails;
    private String merchantPaymentReference;
    private String paymentMethod;
    private String siteId;
    private String merchantConsumerReference;

    protected SaleStatusResponse(Parcel in) {
        merchantPaymentReference = in.readString();
        paymentMethod = in.readString();
        siteId = in.readString();
        merchantConsumerReference = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(merchantPaymentReference);
        dest.writeString(paymentMethod);
        dest.writeString(siteId);
        dest.writeString(merchantConsumerReference);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SaleStatusResponse> CREATOR = new Creator<SaleStatusResponse>() {
        @Override
        public SaleStatusResponse createFromParcel(Parcel in) {
            return new SaleStatusResponse(in);
        }

        @Override
        public SaleStatusResponse[] newArray(int size) {
            return new SaleStatusResponse[size];
        }
    };

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }
}
