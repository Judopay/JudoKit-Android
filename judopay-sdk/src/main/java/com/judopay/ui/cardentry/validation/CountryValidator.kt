package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.ui.cardentry.components.FormFieldEvent
import com.judopay.ui.cardentry.components.FormFieldType

data class CountryValidator(
    override val fieldType: FormFieldType = FormFieldType.COUNTRY
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val isValid = input.isNotBlank()
        val message = if (isValid) R.string.empty else R.string.error_country_should_not_be_empty
        return ValidationResult(isValid, message)
    }
}
