package com.judopay.judokit.android.ui.common

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.os.ConfigurationCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.CountryInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Checks the input string to see whether or not it is a valid Luhn number.
 *
 * @param cardNumber a String that may or may not represent a valid Luhn number
 * @return `true` if and only if the input value is a valid Luhn number
 */
@Suppress("MagicNumber")
internal fun isValidLuhnNumber(cardNumber: String): Boolean {
    var isOdd = true
    var sum = 0

    for (index in cardNumber.length - 1 downTo 0) {
        val c = cardNumber[index]
        if (!Character.isDigit(c)) {
            return false
        }

        var digitInteger = Character.getNumericValue(c)
        isOdd = !isOdd

        if (isOdd) {
            digitInteger *= 2
        }

        if (digitInteger > 9) {
            digitInteger -= 9
        }

        sum += digitInteger
    }

    return sum % 10 == 0
}

fun toDate(
    timestamp: String,
    locale: Locale,
    pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
): Date =
    try {
        val sdf = SimpleDateFormat(pattern, locale)
        sdf.parse(timestamp) ?: Date()
    } catch (exception: ParseException) {
        Log.e("toDate", exception.toString())
        Date()
    }

fun getLocale(resources: Resources): Locale = ConfigurationCompat.getLocales(resources.configuration)[0] ?: Locale.getDefault()

fun getDefaultCountry(context: Context): CountryInfo? {
    val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val simCountryCode =
        telephonyManager?.simCountryIso
    val localeCountryCode =
        getLocale(context.resources).country
    return CountryInfo.list(context).find {
            if (!simCountryCode.isNullOrEmpty()) {
                it.alpha2Code.equals(simCountryCode, ignoreCase = true)
            } else if (!localeCountryCode.isNullOrEmpty()) {
                it.alpha2Code.equals(localeCountryCode, ignoreCase = true)
            } else {
                false
            }
        }
}

fun showAlert(
    context: Context,
    message: String,
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.unable_to_process_request_error_title)
        .setMessage(message)
        .setNegativeButton(R.string.close, null)
        .show()
}

/**
 * Helper function to check if a dependency is present using reflection.
 * @param className name of a class in a dependency package.
 * @return true if present, otherwise false
 */
@Suppress("SwallowedException")
internal fun isDependencyPresent(className: String) =
    try {
        Class.forName(className)
        true
    } catch (e: ClassNotFoundException) {
        Log.i("isDependencyPresent", "Dependency $className is not available in the classpath.")
        false
    }

@Suppress("NestedBlockDepth", "ReturnCount", "CyclomaticComplexMethod")
internal fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result =
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
    } else {
        connectivityManager.run {
            @Suppress("DEPRECATION") // Updated solution implemented for Android API 23+.
            connectivityManager.activeNetworkInfo?.run {
                result =
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
            }
        }
    }
    return result
}
