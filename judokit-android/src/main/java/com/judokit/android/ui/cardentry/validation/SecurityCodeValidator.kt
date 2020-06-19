package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.securityCodeLength
import com.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judokit.android.ui.cardentry.model.FormFieldType

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
