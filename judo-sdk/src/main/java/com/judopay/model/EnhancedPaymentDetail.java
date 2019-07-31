package com.judopay.model;

import com.google.gson.annotations.SerializedName;

public class EnhancedPaymentDetail {
    @SerializedName("SDK_INFO")
    private final SDKInfo sdkInfo;
    @SerializedName("ConsumerDevice")
    private final ConsumerDevice consumerDevice;

    public EnhancedPaymentDetail(final SDKInfo sdkInfo, final ConsumerDevice consumerDevice) {
        this.sdkInfo = sdkInfo;
        this.consumerDevice = consumerDevice;
    }
}
