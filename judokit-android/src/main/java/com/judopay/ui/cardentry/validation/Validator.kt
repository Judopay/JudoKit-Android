package com.judopay.ui.cardentry.validation

import com.judopay.ui.cardentry.components.FormFieldEvent
import com.judopay.ui.cardentry.components.FormFieldType

interface Validator {
    val fieldType: FormFieldType
    fun validate(input: String, formFieldEvent: FormFieldEvent = FormFieldEvent.TEXT_CHANGED): ValidationResult
}
