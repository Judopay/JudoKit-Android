package com.judokit.android.ui.cardentry.validation

import com.judokit.android.ui.cardentry.components.FormFieldEvent
import com.judokit.android.ui.cardentry.components.FormFieldType

interface Validator {
    val fieldType: FormFieldType
    fun validate(input: String, formFieldEvent: FormFieldEvent = FormFieldEvent.TEXT_CHANGED): ValidationResult
}
