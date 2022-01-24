package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

data class PhoneCountryCodeValidator(
    override val fieldType: String = BillingDetailsFieldType.PHONE_COUNTRY_CODE.name
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val isValid = input.length > 4

        return ValidationResult(isValid)
    }
}
