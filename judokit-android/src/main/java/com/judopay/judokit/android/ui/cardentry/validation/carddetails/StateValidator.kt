package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.model.canadaProvincesAndTerritories
import com.judopay.judokit.android.model.usStates
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

data class StateValidator(
    var country: Country? = null,
    override val fieldType: String = BillingDetailsFieldType.STATE.name,
) : Validator {
    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        if (country != Country.CA && country != Country.US) {
            return ValidationResult(true, R.string.jp_empty)
        }
        val validStates = if (country == Country.CA) canadaProvincesAndTerritories else usStates
        val isValid = validStates.map { it.name.lowercase() }.contains(input.lowercase())
        val message =
            when {
                isValid || formFieldEvent == FormFieldEvent.TEXT_CHANGED -> R.string.jp_empty
                country == Country.CA -> R.string.jp_error_province_territory_should_not_be_empty
                else -> R.string.jp_error_state_should_not_be_empty
            }
        return ValidationResult(isValid, message)
    }
}
