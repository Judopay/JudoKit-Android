package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

private const val MIN_PHONE_COUNTRY_CODE_LENGTH = 4

data class PhoneCountryCodeValidator(
    override val fieldType: String = BillingDetailsFieldType.PHONE_COUNTRY_CODE.name,
) : Validator {
    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val isValid = input.length >= MIN_PHONE_COUNTRY_CODE_LENGTH

        return ValidationResult(isValid)
    }
}
