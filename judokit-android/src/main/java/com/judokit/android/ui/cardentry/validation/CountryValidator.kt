package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judokit.android.ui.cardentry.model.FormFieldType

data class CountryValidator(
    override val fieldType: FormFieldType = FormFieldType.COUNTRY
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val isValid = input.isNotBlank()
        val message = if (isValid) R.string.empty else R.string.error_country_should_not_be_empty
        return ValidationResult(isValid, message)
    }
}
