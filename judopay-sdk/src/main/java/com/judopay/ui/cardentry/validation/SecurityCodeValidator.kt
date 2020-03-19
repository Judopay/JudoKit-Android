package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.securityCodeLength
import com.judopay.ui.cardentry.components.FormFieldType

data class SecurityCodeValidator(
    var cardNetwork: CardNetwork? = null,
    override val fieldType: FormFieldType = FormFieldType.SECURITY_NUMBER
) : Validator {

    override fun validate(input: String): ValidationResult {
        val requiredLength = cardNetwork?.securityCodeLength ?: 3
        val isValid = input.length == requiredLength
        val message = if (isValid) R.string.empty else R.string.check_cvv
        return ValidationResult(isValid, message)
    }
}
