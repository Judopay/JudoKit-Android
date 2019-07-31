package com.judopay.model;

import com.google.gson.annotations.SerializedName;

public class ConsumerDevice {
    @SerializedName("ClientDetails")
    private final ClientDetails clientDetails;
    @SerializedName("IpAddress")
    private final String ipAddress;
    @SerializedName("GeoLocation")
    private final GeoLocation geolocation;
    @SerializedName("ThreeDSecure")
    private final ThreeDSecure threeDSecure;
    @SerializedName("PaymentType")
    private final String paymentType = "ECOMM";

    public ConsumerDevice(final String ipAddress, final ClientDetails clientDetails, final GeoLocation geolocation, final ThreeDSecure threeDSecure) {
        this.ipAddress = ipAddress;
        this.clientDetails = clientDetails;
        this.geolocation = geolocation;
        this.threeDSecure = threeDSecure;
    }
}
