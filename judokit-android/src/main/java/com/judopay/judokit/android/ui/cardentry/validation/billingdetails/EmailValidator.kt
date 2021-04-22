package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import android.util.Patterns
import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

data class EmailValidator(
    override val fieldType: String = BillingDetailsFieldType.EMAIL.name
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(input).matches()
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message = if (shouldDisplayMessage) {
            R.string.invalid_email_address
        } else {
            R.string.empty
        }
        return ValidationResult(isValid, message)
    }
}
