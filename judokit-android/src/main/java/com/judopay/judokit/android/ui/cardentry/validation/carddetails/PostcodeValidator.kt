package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH
import com.judopay.judokit.android.ui.common.REG_EX_CA_POST_CODE
import com.judopay.judokit.android.ui.common.REG_EX_GB_POST_CODE
import com.judopay.judokit.android.ui.common.REG_EX_US_POST_CODE

open class PostcodeValidator(
    open var country: Country? = null,
    override val fieldType: String = BillingDetailsFieldType.POST_CODE.name
) : Validator {

    private val postCodeGBRegEx = REG_EX_GB_POST_CODE.toRegex()
    private val postCodeUSRegEx = REG_EX_US_POST_CODE.toRegex()
    private val postCodeCARegEx = REG_EX_CA_POST_CODE.toRegex()

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val isValid = isPostCodeValid(input)
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED

        val message = if (shouldDisplayMessage) {
            if (isValid) R.string.empty else errorString()
        } else {
            R.string.empty
        }

        return ValidationResult(isValid, message)
    }

    private fun errorString(): Int = when (country) {
        Country.US -> R.string.invalid_zip_code
        else -> R.string.invalid_postcode
    }

    private fun isPostCodeValid(input: String): Boolean {
        if (input.replace("\\s".toRegex(), "").length > POSTAL_CODE_MAX_LENGTH) {
            return false
        }

        return when (country) {
            Country.GB -> input.matches(postCodeGBRegEx)
            Country.US -> input.matches(postCodeUSRegEx)
            Country.CA -> input.matches(postCodeCARegEx)
            Country.OTHER -> input.isNotBlank()
            else -> false
        }
    }
}
