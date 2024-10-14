package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.REG_EX_MOBILE_NUMBER

data class MobileNumberValidator(
    override val fieldType: String = BillingDetailsFieldType.MOBILE_NUMBER.name,
) : Validator {
    private val mobileNumberRegEx = REG_EX_MOBILE_NUMBER.toRegex()

    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message =
            if (shouldDisplayMessage) {
                R.string.jp_invalid_mobile_number
            } else {
                R.string.jp_empty
            }
        return ValidationResult(mobileNumberRegEx.matches(input), message)
    }
}
