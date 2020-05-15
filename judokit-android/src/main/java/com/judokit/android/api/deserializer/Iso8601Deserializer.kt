package com.judokit.android.api.deserializer

import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.TimeZone

object Iso8601Deserializer {

    fun toDate(toParse: String): Date {
        return toCalendar(toParse).time
    }

    private fun toCalendar(toParse: String): Calendar {
        if (toParse.indexOf('T') == -1) {
            return buildCalendarWithDateOnly(toParse, toParse)
        }
        val indexOfT = toParse.indexOf('T')
        val result = buildCalendarWithDateOnly(toParse.substring(0, indexOfT), toParse)
        return parseHour(result, toParse.substring(indexOfT + 1))
    }

    private fun parseHour(result: Calendar, hourStr: String): Calendar {
        val basicFormatHour = hourStr.replace(":", "")
        val indexOfZ = basicFormatHour.indexOf('Z')
        if (indexOfZ != -1) {
            parseHourWithoutHandlingTimeZone(result, basicFormatHour.substring(0, indexOfZ))
        } else {
            val indexOfSign = getIndexOfSign(basicFormatHour)
            if (indexOfSign == -1) {
                parseHourWithoutHandlingTimeZone(result, basicFormatHour)
                result.timeZone = TimeZone.getDefault()
            } else {
                parseHourWithoutHandlingTimeZone(result, basicFormatHour.substring(0, indexOfSign))
                result.timeZone = TimeZone.getTimeZone("GMT" + basicFormatHour.substring(indexOfSign))
            }
        }
        return result
    }

    private fun getIndexOfSign(str: String): Int {
        val index = str.indexOf('+')
        return if (index != -1) index else str.indexOf('-')
    }

    private fun parseHourWithoutHandlingTimeZone(calendar: Calendar, basicFormatHour: String) {
        var formatHour = basicFormatHour

        formatHour = formatHour.replace(',', '.')

        val indexOfDot = formatHour.indexOf('.')
        var fractionalPart = 0.0

        if (indexOfDot != -1) {
            fractionalPart = ("0" + formatHour.substring(indexOfDot)).toDouble()
            formatHour = formatHour.substring(0, indexOfDot)
        }

        if (formatHour.length >= 2) {
            calendar[Calendar.HOUR_OF_DAY] = formatHour.substring(0, 2).toInt()
        }

        if (formatHour.length > 2) {
            calendar[Calendar.MINUTE] = formatHour.substring(2, 4).toInt()
        } else {
            fractionalPart *= 60.0
        }

        if (formatHour.length > 4) {
            calendar[Calendar.SECOND] = formatHour.substring(4, 6).toInt()
        } else {
            fractionalPart *= 60.0
        }

        calendar[Calendar.MILLISECOND] = (fractionalPart * 1000).toInt()
    }

    private fun buildCalendarWithDateOnly(dateStr: String, originalDate: String): Calendar {
        val result: Calendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        result.minimalDaysInFirstWeek = 4
        result.firstDayOfWeek = Calendar.MONDAY
        result[Calendar.HOUR_OF_DAY] = 0
        result[Calendar.MINUTE] = 0
        result[Calendar.SECOND] = 0
        result[Calendar.MILLISECOND] = 0
        val basicFormatDate = dateStr.replace("-".toRegex(), "")

        return when {
            basicFormatDate.indexOf('W') != -1 -> parseWeekDate(result, basicFormatDate)
            basicFormatDate.length == 7 -> parseOrdinalDate(result, basicFormatDate)
            else -> parseCalendarDate(result, basicFormatDate, originalDate)
        }
    }

    private fun parseCalendarDate(result: Calendar, basicFormatDate: String, originalDate: String): Calendar {
        return when (basicFormatDate.length) {
            2 -> parseCalendarDateWithCenturyOnly(result, basicFormatDate)
            4 -> parseCalendarDateWithYearOnly(result, basicFormatDate)
            else -> parseCalendarDateWithPrecisionGreaterThanYear(result, basicFormatDate, originalDate)
        }
    }

    private fun parseCalendarDateWithCenturyOnly(result: Calendar, basicFormatDate: String): Calendar {
        result[basicFormatDate.toInt() * 100, 0] = 1
        return result
    }

    private fun parseCalendarDateWithYearOnly(result: Calendar, basicFormatDate: String): Calendar {
        result[basicFormatDate.toInt(), 0] = 1
        return result
    }

    private fun parseCalendarDateWithPrecisionGreaterThanYear(result: Calendar, basicFormatDate: String, originalDate: String): Calendar {
        val year = basicFormatDate.substring(0, 4).toInt()
        val month = basicFormatDate.substring(4, 6).toInt() - 1
        if (basicFormatDate.length == 6) {
            result[year, month] = 1
            return result
        }
        if (basicFormatDate.length == 8) {
            result[year, month] = basicFormatDate.substring(6).toInt()
            return result
        }
        throw RuntimeException("Can't parse $originalDate")
    }

    private fun parseWeekDate(result: Calendar, basicFormatDate: String): Calendar {
        result[Calendar.YEAR] = basicFormatDate.substring(0, 4).toInt()
        result[Calendar.WEEK_OF_YEAR] = basicFormatDate.substring(5, 7).toInt()
        result[Calendar.DAY_OF_WEEK] = if (basicFormatDate.length == 7) Calendar.MONDAY else Calendar.SUNDAY + basicFormatDate.substring(7).toInt()
        return result
    }

    private fun parseOrdinalDate(calendar: Calendar, basicFormatOrdinalDate: String): Calendar {
        calendar[Calendar.YEAR] = basicFormatOrdinalDate.substring(0, 4).toInt()
        calendar[Calendar.DAY_OF_YEAR] = basicFormatOrdinalDate.substring(4).toInt()
        return calendar
    }
}
