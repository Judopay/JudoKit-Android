package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.securityCodeLength
import com.judopay.ui.cardentry.components.FormFieldEvent
import com.judopay.ui.cardentry.components.FormFieldType

data class SecurityCodeValidator(
    var cardNetwork: CardNetwork? = null,
    override val fieldType: FormFieldType = FormFieldType.SECURITY_NUMBER
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val requiredLength = cardNetwork?.securityCodeLength ?: 3
        val isValid = input.length == requiredLength
        val message = if (!isValid && formFieldEvent == FormFieldEvent.FOCUS_CHANGED)
            R.string.check_cvv else R.string.empty

        return ValidationResult(isValid, message)
    }
}
