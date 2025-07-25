package com.judopay.judokit.android.api.model.response

import java.util.Calendar
import java.util.Date

@Suppress("MagicNumber")
class CardDate(
    date: String = "",
) {
    var date: String = date
        set(value) {
            field = value
            splitCardDate(field)
        }

    internal var month: Int = 0
    internal var year: Int = 0

    private fun getYear(year: String): Int =
        if (isDateInvalid(year)) {
            0
        } else {
            2000 + year.substring(2, 4).toInt()
        }

    private fun getMonth(month: String): Int =
        if (isDateInvalid(month)) {
            0
        } else {
            month.substring(0, 2).toInt()
        }

    val isBeforeToday: Boolean
        get() {
            if (year == 0 || month == 0) {
                return false
            }
            val cardDate = Calendar.getInstance()
            cardDate[year, month - 1] = 1
            val now = Calendar.getInstance()
            now.time = Date()
            return cardDate.before(now)
        }

    val isAfterToday: Boolean
        get() {
            if (year == 0 || month == 0) {
                return false
            }
            val cardDate = Calendar.getInstance()
            cardDate[Calendar.YEAR] = year
            cardDate[Calendar.MONTH] = month - 1
            cardDate[Calendar.DATE] = cardDate.getActualMaximum(Calendar.DAY_OF_MONTH)
            val now = Calendar.getInstance()
            now.time = Date()
            return cardDate.after(now)
        }

    val isInsideAllowedDateRange: Boolean
        get() {
            val minDate = Calendar.getInstance()
            minDate[Calendar.YEAR] = minDate[Calendar.YEAR] - 10
            val maxDate = Calendar.getInstance()
            maxDate[Calendar.YEAR] = maxDate[Calendar.YEAR] + 10
            val cardDate = Calendar.getInstance()
            cardDate[year, month - 1] = 1
            return cardDate.after(minDate) && cardDate.before(maxDate)
        }

    val isExpiredInTwoMonths: Boolean
        get() {
            val maxDate = Calendar.getInstance()
            maxDate[Calendar.MONTH] = maxDate[Calendar.MONTH] + 2

            val cardDate = Calendar.getInstance()
            cardDate[year, month] = 2

            val now =
                Calendar.getInstance().apply {
                    time = Date()
                }
            now[Calendar.MONTH] = now[Calendar.MONTH] - 2
            return cardDate.after(now) && cardDate.before(maxDate)
        }

    private fun isDateInvalid(date: String): Boolean = !date.matches("(?:0[1-9]|1[0-2])[0-9]{2}".toRegex())

    private fun splitCardDate(cardDate: String) {
        val splitCardDate = cardDate.replace("/".toRegex(), "")
        month = getMonth(splitCardDate)
        year = getYear(splitCardDate)
    }

    init {
        splitCardDate(date)
    }
}
