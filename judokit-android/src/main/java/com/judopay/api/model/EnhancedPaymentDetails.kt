package com.judopay.api.model

import com.google.gson.annotations.SerializedName

class SDKInfo(
    @field:SerializedName("Version") private val version: String,
    @field:SerializedName("Name") private val name: String
)

class ConsumerDevice(
    @field:SerializedName("IpAddress") val ipAddress: String,
    @field:SerializedName("ClientDetails") val clientDetails: ClientDetails,
    @field:SerializedName("GeoLocation") val geolocation: GeoLocation,
    @field:SerializedName("ThreeDSecure") val threeDSecure: ThreeDSecure,
    @field:SerializedName("PaymentType") val paymentType: String = "ECOMM"
)

class ClientDetails(
    val key: String?,
    val value: String?
)

class GeoLocation(private val latitude: Double, private val longitude: Double)

class ThreeDSecure(@field:SerializedName("Browser") private val browser: Browser)

class Browser(
    @field:SerializedName("Language") private val language: String,
    @field:SerializedName("ScreenHeight") private val screenHeight: String,
    @field:SerializedName("ScreenWidth") private val screeWidth: String,
    @field:SerializedName("TimeZone") private val timeZone: String,
    @field:SerializedName("UserAgent") private val userAgent: String
) {

    @SerializedName("AcceptHeader")
    private val acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"

    @SerializedName("JavaEnabled")
    private val javaEnabled = "false"

    @SerializedName("JavascriptEnabled")
    private val javaScriptEnabled = "true"

    @SerializedName("ColorDepth")
    private val colorDepth = "32"
}

class EnhancedPaymentDetail(
    @field:SerializedName("SDK_INFO") val sdkInfo: SDKInfo,
    @field:SerializedName("ConsumerDevice") val consumerDevice: ConsumerDevice
)
