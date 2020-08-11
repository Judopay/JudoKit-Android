package com.judokit.android.ui.common

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.core.os.ConfigurationCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judokit.android.Judo
import com.judokit.android.R
import com.judokit.android.api.model.request.Address
import com.judokit.android.api.model.request.BankSaleRequest
import com.judokit.android.api.model.request.CheckCardRequest
import com.judokit.android.api.model.request.GooglePayRequest
import com.judokit.android.api.model.request.GooglePayWallet
import com.judokit.android.api.model.request.IdealSaleRequest
import com.judokit.android.api.model.request.PaymentRequest
import com.judokit.android.api.model.request.RegisterCardRequest
import com.judokit.android.api.model.request.SaveCardRequest
import com.judokit.android.api.model.request.TokenRequest
import com.judokit.android.toMap
import java.math.BigDecimal
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
    pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
): Date = try {
    val sdf = SimpleDateFormat(pattern, locale)
    sdf.parse(timestamp) ?: Date()
} catch (exception: ParseException) {
    Log.e("toDate", exception.toString())
    Date()
}

fun getLocale(resources: Resources): Locale =
    ConfigurationCompat.getLocales(resources.configuration)[0]

fun showAlert(context: Context, message: String) {
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.unable_to_process_request_error_title)
        .setMessage(message)
        .setNegativeButton(R.string.close, null)
        .show()
}
