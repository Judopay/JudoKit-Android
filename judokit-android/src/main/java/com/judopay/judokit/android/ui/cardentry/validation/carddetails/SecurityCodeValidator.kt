package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.securityCodeInvalidResId
import com.judopay.judokit.android.model.securityCodeLength
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

data class SecurityCodeValidator(
    var cardNetwork: CardNetwork? = null,
    override val fieldType: String = CardDetailsFieldType.SECURITY_NUMBER.name
) : Validator {
    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val requiredLength = cardNetwork?.securityCodeLength ?: 3
        val isLengthValid = input.length == requiredLength
        val shouldDisplayMessage = !isLengthValid && formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message = if (shouldDisplayMessage) {
            cardNetwork?.securityCodeInvalidResId ?: R.string.empty
        } else {
            R.string.empty
        }

        return ValidationResult(isLengthValid, message)
    }
}
