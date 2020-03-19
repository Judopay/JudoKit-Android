package com.judopay.ui.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
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

fun isExpired(expireDate: String, pattern: String = "MM/yy"): Boolean {
    return try {
        val date = SimpleDateFormat(pattern, Locale.UK).parse(expireDate)
        date?.before(Date()) ?: false
    } catch (exception: ParseException) {
        exception.printStackTrace()
        false
    }
}

fun isExpiredInTwoMonths(expireDate: String, pattern: String = "MM/yy"): Boolean {
    val twoMonths = Calendar.getInstance().apply { add(Calendar.MONTH, 2) }.time
    return try {
        val parsedExpireDate = SimpleDateFormat(pattern, Locale.UK).parse(expireDate)
        val isAfterToday = parsedExpireDate?.after(Date()) ?: false
        val isBeforeTwoMonths = parsedExpireDate?.before(twoMonths) ?: false
        isAfterToday && isBeforeTwoMonths
    } catch (exception: ParseException) {
        exception.printStackTrace()
        false
    }
}