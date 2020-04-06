package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.api.model.response.CardDate
import com.judopay.ui.cardentry.components.FormFieldEvent
import com.judopay.ui.cardentry.components.FormFieldType

class ExpirationDateValidator(override val fieldType: FormFieldType = FormFieldType.EXPIRATION_DATE) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val date = CardDate(input)
        val isValid = date.isAfterToday && date.isInsideAllowedDateRange
        val showError = input.length == 5

        return ValidationResult(isValid, if (isValid || !showError) R.string.empty else R.string.check_expiry_date)
    }
}
