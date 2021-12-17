package com.judopay.judokit.android.api

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION

class AppMetaDataProvider(context: Context) {

    private val applicationContext = context.applicationContext
    private val appVersion: String
        get() = try {
            val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
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
            return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "Unknown") as String
        }

    val userAgent: String
        get() =
            """Android/$JUDO_KIT_VERSION ${Build.MANUFACTURER} ${Build.MODEL} $appName $appVersion""".trimMargin()
}
