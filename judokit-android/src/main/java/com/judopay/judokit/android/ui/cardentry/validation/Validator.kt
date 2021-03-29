package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.FormFieldType

interface Validator {
    val fieldType: FormFieldType
    fun validate(input: String, formFieldEvent: FormFieldEvent = FormFieldEvent.TEXT_CHANGED): ValidationResult
}
