package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.REG_EX_CITY

data class CityValidator(
    override val fieldType: String = BillingDetailsFieldType.CITY.name,
) : Validator {
    private val cityNameRegEx = REG_EX_CITY.toRegex()

    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message =
            if (shouldDisplayMessage) {
                R.string.invalid_city
            } else {
                R.string.empty
            }
        return ValidationResult(cityNameRegEx.matches(input), message)
    }
}
