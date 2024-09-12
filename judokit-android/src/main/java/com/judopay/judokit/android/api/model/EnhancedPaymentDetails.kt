package com.judopay.judokit.android.api.model

import com.google.gson.annotations.SerializedName

class SDKInfo(
    @field:SerializedName("Version") private val version: String,
    @field:SerializedName("Name") private val name: String,
)

class ConsumerDevice(
    @field:SerializedName("ThreeDSecure") val threeDSecure: ThreeDSecure,
)

class ThreeDSecure(
    @field:SerializedName("Browser") private val browser: Browser,
)

class Browser(
    @field:SerializedName("UserAgent") private val userAgent: String,
)

class EnhancedPaymentDetail(
    @field:SerializedName("SDK_INFO") val sdkInfo: SDKInfo,
    @field:SerializedName("ConsumerDevice") val consumerDevice: ConsumerDevice,
)
