package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator

class BillingDetailsPostCodeValidator(
    override val fieldType: String = BillingDetailsFieldType.POST_CODE.name
) : Validator {

    private val regex = Regex("^[a-zA-Z0-9]+\$")

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message = if (shouldDisplayMessage) {
            R.string.invalid_postcode
        } else {
            R.string.empty
        }
        return ValidationResult(regex.matches(input), message)
    }
}
