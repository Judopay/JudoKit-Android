package com.judopay.judokit.android.api

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.judopay.judokit.android.model.SubProductInfo
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION

class AppMetaDataProvider(
    context: Context,
    subProductInfo: SubProductInfo,
) {
    object SystemInfo {
        val androidVersionString: String? = Build.VERSION.RELEASE
        val deviceManufacturer: String? = Build.MANUFACTURER
        val deviceModel: String? = Build.MODEL
    }

    private val applicationContext = context.applicationContext

    @Suppress("SwallowedException")
    private val appVersion: String
        get() =
            try {
                val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
                packageInfo.versionName ?: ""
            } catch (e: PackageManager.NameNotFoundException) {
                ""
            }

    private val appName: String
        get() {
            val packageManager = applicationContext.packageManager
            var applicationInfo: ApplicationInfo? = null
            try {
                applicationInfo = packageManager.getApplicationInfo(applicationContext.applicationInfo.packageName, 0)
            } catch (ignore: PackageManager.NameNotFoundException) {
            }
            return if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) as String else "Unknown"
        }

    private val subProductAndVersion =
        when (subProductInfo) {
            is SubProductInfo.Unknown -> ""
            is SubProductInfo.ReactNative -> "(JudoKit-ReactNative/${subProductInfo.version}) "
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    val userAgent: String
        get() =
            "JudoKit-Android/$JUDO_KIT_VERSION ${subProductAndVersion}Android/${SystemInfo.androidVersionString} $appName/$appVersion ${SystemInfo.deviceManufacturer} ${SystemInfo.deviceModel}"
}
