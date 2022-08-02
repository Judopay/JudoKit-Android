package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

data class MobileNumberValidator(
    override val fieldType: String = BillingDetailsFieldType.MOBILE_NUMBER.name
) : Validator {

    private val regex = Regex("^.{10,}\$")

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message = if (shouldDisplayMessage) {
            R.string.invalid_mobile_number
        } else {
            R.string.empty
        }
        return ValidationResult(regex.matches(input), message)
    }
}