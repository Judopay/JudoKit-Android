package com.judopay.api

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class AppMetaDataProvider(context: Context) {

    private val applicationContext = context.applicationContext
    val appVersion: String
        get() = try {
            val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }

    val appName: String
        get() {
            val packageManager = applicationContext.packageManager
            var applicationInfo: ApplicationInfo? = null
            try {
                applicationInfo = packageManager.getApplicationInfo(applicationContext.applicationInfo.packageName, 0)
            } catch (ignore: PackageManager.NameNotFoundException) {
            }
            return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "Unknown") as String
        }
}
