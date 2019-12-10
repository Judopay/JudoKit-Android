package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetails implements Parcelable {
    private String orderId;
    private OrderStatus orderStatus;
    private String orderFailureReason;
    private String timestamp;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    protected OrderDetails(Parcel in) {
        orderId = in.readString();
        orderFailureReason = in.readString();
        timestamp = in.readString();
        orderStatus = (OrderStatus) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(orderFailureReason);
        dest.writeString(timestamp);
        dest.writeSerializable(orderStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderDetails> CREATOR = new Creator<OrderDetails>() {
        @Override
        public OrderDetails createFromParcel(Parcel in) {
            return new OrderDetails(in);
        }

        @Override
        public OrderDetails[] newArray(int size) {
            return new OrderDetails[size];
        }
    };
}
