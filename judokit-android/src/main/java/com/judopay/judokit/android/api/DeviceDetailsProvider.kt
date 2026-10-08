package com.judopay.judokit.android.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.judopay.judokit.android.api.model.request.DeviceDetails
import java.util.Locale
import java.util.UUID

private const val SHARED_PREFS_NAME = "DeviceDNA"
private const val V_DEVICE_ID_KEY = "Judo-vDeviceId"

internal class DeviceDetailsProvider(
    context: Context,
    androidVersion: String? = Build.VERSION.RELEASE,
) {
    private val applicationContext = context.applicationContext

    val deviceDetails: DeviceDetails
        get() =
            DeviceDetails(
                kDeviceId = kDeviceId,
                vDeviceId = vDeviceId,
                countryCode = tryOrNull { countryCode },
                cultureLocale = tryOrNull { cultureLocale },
                os = os,
            )

    // A failing prop gathering must never fail the request this object is attached to.
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private fun tryOrNull(value: () -> String?): String? =
        try {
            value()
        } catch (ignored: Exception) {
            null
        }

    private val kDeviceId: String? by lazy { tryOrNull(::androidId) }

    @SuppressLint("HardwareIds")
    private fun androidId(): String? = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

    private val vDeviceId: String? by lazy {
        tryOrNull {
            val sharedPrefs = applicationContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

            sharedPrefs.getString(V_DEVICE_ID_KEY, null)
                ?: UUID.randomUUID().toString().also {
                    sharedPrefs.edit { putString(V_DEVICE_ID_KEY, it) }
                }
        }
    }

    private val os: String = "Android $androidVersion"

    // Computed because the user can change the device locale mid-session.
    private val countryCode: String
        get() = Locale.getDefault().country

    private val cultureLocale: String
        get() = Locale.getDefault().run { "${language}_$country" }
}
