package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.FormFieldType
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

data class CardHolderNameValidator(override val fieldType: String = FormFieldType.HOLDER_NAME.name) :
    Validator {
    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        return ValidationResult(input.length > 3)
    }
}
