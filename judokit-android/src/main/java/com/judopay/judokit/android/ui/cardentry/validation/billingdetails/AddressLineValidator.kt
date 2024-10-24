package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.REG_EX_ADDRESS_LINE

data class AddressLineValidator(
    override val fieldType: String = BillingDetailsFieldType.ADDRESS_LINE_1.name,
) : Validator {
    private val addressLineRegEx = REG_EX_ADDRESS_LINE.toRegex()

    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED
        val message =
            if (shouldDisplayMessage) {
                R.string.jp_invalid_address
            } else {
                R.string.jp_empty
            }
        return ValidationResult(addressLineRegEx.matches(input), message)
    }
}
