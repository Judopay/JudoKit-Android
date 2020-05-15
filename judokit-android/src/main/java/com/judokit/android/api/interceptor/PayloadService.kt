package com.judokit.android.api.interceptor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.DisplayMetrics
import android.view.WindowManager
import android.webkit.WebSettings
import com.judokit.android.BuildConfig
import com.judokit.android.R
import com.judokit.android.api.model.Browser
import com.judokit.android.api.model.ClientDetails
import com.judokit.android.api.model.ConsumerDevice
import com.judokit.android.api.model.EnhancedPaymentDetail
import com.judokit.android.api.model.GeoLocation
import com.judokit.android.api.model.SDKInfo
import com.judokit.android.api.model.ThreeDSecure
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
    fun getEnhancedPaymentDetail(): EnhancedPaymentDetail? =
        EnhancedPaymentDetail(getSdkInfo(), getConsumerDevice())

    private fun getSdkInfo(): SDKInfo {
        return SDKInfo(BuildConfig.VERSION_NAME, context.getString(R.string.judokit_android))
    }

    private fun getConsumerDevice(): ConsumerDevice = ConsumerDevice(
        getIPAddress(),
        getClientDetails(),
        getGeolocation(),
        getThreeDSecureInfo()
    )

    private fun getClientDetails(): ClientDetails {
        val deviceDNA = DeviceDNA(context)
        val mapDeviceDna =
            deviceDNA.deviceDNA
        return ClientDetails(mapDeviceDna["key"], mapDeviceDna["value"])
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

    private fun getThreeDSecureInfo(): ThreeDSecure = ThreeDSecure(getBrowserInfo())

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
