package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.ui.cardentry.components.FormFieldType
import java.util.*


const val MAX_YEARS = 10

class ExpirationDateValidator(override val fieldType: FormFieldType = FormFieldType.EXPIRATION_DATE) : Validator {

    private fun isAfterToday(year: Int, month: Int): Boolean {

        if (year == 0 || month == 0) {
            return false
        }

        val cardDate = Calendar.getInstance().also {
            it[Calendar.YEAR] = year
            it[Calendar.MONTH] = month - 1
            it[Calendar.DATE] = it.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        val now = Calendar.getInstance().apply {
            time = Date()
        }

        return cardDate.after(now)
    }

    private fun isInsideAllowedDateRange(year: Int, month: Int): Boolean {
        val minDate = Calendar.getInstance()
        minDate[Calendar.YEAR] = minDate[Calendar.YEAR] - MAX_YEARS

        val maxDate = Calendar.getInstance()
        maxDate[Calendar.YEAR] = maxDate[Calendar.YEAR] + MAX_YEARS

        val cardDate = Calendar.getInstance()
        cardDate[year, month - 1] = 1

        return cardDate.after(minDate) && cardDate.before(maxDate)
    }

    override fun validate(input: String): ValidationResult {
        val slash = "/"
        val isDateInvalid = !input.replace(slash.toRegex(), "").matches("(?:0[1-9]|1[0-2])[0-9]{2}".toRegex())
        val splitCardDate = input.split(slash.toRegex())

        val month = if (isDateInvalid) 0 else splitCardDate.first().toInt()
        val year = if (isDateInvalid) 0 else 2000 + splitCardDate.last().toInt()

        val isValid = isAfterToday(year, month) && isInsideAllowedDateRange(year, month)
        return ValidationResult(isValid, if (isValid) R.string.empty else R.string.check_expiry_date)
    }

}
