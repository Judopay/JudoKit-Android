package com.judopay.api.interceptor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.DisplayMetrics
import android.view.WindowManager
import android.webkit.WebSettings
import com.judopay.BuildConfig
import com.judopay.R
import com.judopay.api.model.Browser
import com.judopay.api.model.ClientDetails
import com.judopay.api.model.ConsumerDevice
import com.judopay.api.model.EnhancedPaymentDetail
import com.judopay.api.model.GeoLocation
import com.judopay.api.model.SDKInfo
import com.judopay.api.model.ThreeDSecure
import com.judopay.devicedna.DeviceDNA
import com.judopay.devicedna.PermissionUtil
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import java.util.Locale
import java.util.TimeZone

class PayloadService(private val context: Context) {
    fun getEnhancedPaymentDetail(): EnhancedPaymentDetail? {
        val sdkInfo = getSdkInfo()
        val consumerDevice: ConsumerDevice = getConsumerDevice()
        return EnhancedPaymentDetail(sdkInfo, consumerDevice)
    }

    private fun getSdkInfo(): SDKInfo {
        return SDKInfo(BuildConfig.VERSION_NAME, context.getString(R.string.judokit_android))
    }

    private fun getConsumerDevice(): ConsumerDevice {
        val clientDetails: ClientDetails = getClientDetails()
        val geolocation: GeoLocation = getGeolocation()
        val threeDSecure: ThreeDSecure = getThreeDSecureInfo()
        val ipAddress: String = getIPAddress()
        return ConsumerDevice(ipAddress, clientDetails, geolocation, threeDSecure)
    }

    private fun getClientDetails(): ClientDetails {
        val deviceDNA = DeviceDNA(context)
        val mapDeviceDna =
            deviceDNA.deviceDNA
        return ClientDetails(mapDeviceDna["key"]!!, mapDeviceDna["value"]!!)
    }

    private fun getGeolocation(): GeoLocation {
        val lastKnowLocation: Location? = getLastKnownLocation()
        val latitude =
            lastKnowLocation?.latitude ?: 0.0
        val longitude =
            lastKnowLocation?.longitude ?: 0.0
        return GeoLocation(latitude, longitude)
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val manager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = manager.getProviders(true)
        var lastKnownLocation: Location? = null
        for (provider in providers) {
            val accessFineLocation = PermissionUtil.isPermissionGranted(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val accessCoarseLocation = PermissionUtil.isPermissionGranted(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (accessCoarseLocation || accessFineLocation) {
                if (lastKnownLocation == null) {
                    lastKnownLocation = manager.getLastKnownLocation(provider)
                } else {
                    val location =
                        manager.getLastKnownLocation(provider)
                    if (location != null && location.time > lastKnownLocation.time) {
                        lastKnownLocation = location
                    }
                }
            }
        }
        return lastKnownLocation
    }

    private fun getThreeDSecureInfo(): ThreeDSecure {
        val browser: Browser = getBrowserInfo()
        return ThreeDSecure(browser)
    }

    private fun getBrowserInfo(): Browser {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val defaultTimeZone: TimeZone = TimeZone.getDefault()
        val deviceLanguage: String = Locale.getDefault().language
        val screenHeight = metrics.heightPixels.toString()
        val screenWidth = metrics.widthPixels.toString()
        val timeZone: String = defaultTimeZone.getDisplayName(false, TimeZone.SHORT)
        val userAgent = WebSettings.getDefaultUserAgent(context)
        return Browser(deviceLanguage, screenHeight, screenWidth, timeZone, userAgent)
    }

    private fun getIPAddress(): String {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return ""
    }
}
