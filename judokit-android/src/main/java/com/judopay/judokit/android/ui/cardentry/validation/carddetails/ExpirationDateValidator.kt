package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

class ExpirationDateValidator(
    override val fieldType: String = CardDetailsFieldType.EXPIRATION_DATE.name,
    val cardDate: CardDate = CardDate()
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val date = cardDate.apply { cardDate = input }
        val isValid = date.isAfterToday && date.isInsideAllowedDateRange
        val shouldNotDisplayMessage = formFieldEvent != FormFieldEvent.FOCUS_CHANGED

        val message = when {
            shouldNotDisplayMessage -> R.string.empty
            !isValid -> R.string.check_expiry_date
            else -> R.string.empty
        }

        return ValidationResult(isValid, message)
    }
}
