package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.api.model.response.CardDate
import com.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judokit.android.ui.cardentry.model.FormFieldType

class ExpirationDateValidator(
    override val fieldType: FormFieldType = FormFieldType.EXPIRATION_DATE,
    val cardDate: CardDate = CardDate()
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val date = cardDate.apply { cardDate = input }
        val isValid = date.isAfterToday && date.isInsideAllowedDateRange
        val showError = input.length == 5

        return ValidationResult(
            isValid,
            if (isValid || !showError) R.string.empty else R.string.check_expiry_date
        )
    }
}
