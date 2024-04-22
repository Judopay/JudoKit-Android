package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent

interface Validator {
    val fieldType: String

    fun validate(
        input: String,
        formFieldEvent: FormFieldEvent = FormFieldEvent.TEXT_CHANGED,
    ): ValidationResult
}
