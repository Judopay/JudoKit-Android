package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_CA
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_OTHER
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_UK
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_USA
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MIN_LENGTH_CA
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MIN_LENGTH_OTHER
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MIN_LENGTH_UK
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MIN_LENGTH_USA
import com.judopay.judokit.android.ui.common.REG_EX_CA_POST_CODE
import com.judopay.judokit.android.ui.common.REG_EX_GB_POST_CODE
import com.judopay.judokit.android.ui.common.REG_EX_OTHER_POST_CODE
import com.judopay.judokit.android.ui.common.REG_EX_US_POST_CODE

open class PostcodeValidator(
    open var country: Country? = null,
    override val fieldType: String = BillingDetailsFieldType.POST_CODE.name,
) : Validator {
    private val postCodeGBRegEx = REG_EX_GB_POST_CODE.toRegex(RegexOption.IGNORE_CASE)
    private val postCodeUSRegEx = REG_EX_US_POST_CODE.toRegex(RegexOption.IGNORE_CASE)
    private val postCodeCARegEx = REG_EX_CA_POST_CODE.toRegex(RegexOption.IGNORE_CASE)
    private val postCodeOtherRegEx = REG_EX_OTHER_POST_CODE.toRegex(RegexOption.IGNORE_CASE)

    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val isValid = isPostCodeValid(input)
        val shouldDisplayMessage = formFieldEvent == FormFieldEvent.FOCUS_CHANGED

        val message =
            if (shouldDisplayMessage) {
                if (isValid) R.string.jp_empty else errorString()
            } else {
                R.string.jp_empty
            }

        return ValidationResult(isValid, message)
    }

    private fun errorString(): Int =
        when (country) {
            Country.US -> R.string.jp_invalid_zip_code
            else -> R.string.jp_invalid_postcode
        }

    private fun isPostCodeValid(input: String): Boolean {
        return when (country) {
            Country.GB -> input.length in POSTAL_CODE_MIN_LENGTH_UK..POSTAL_CODE_MAX_LENGTH_UK && input.matches(postCodeGBRegEx)
            Country.US -> input.length in POSTAL_CODE_MIN_LENGTH_USA..POSTAL_CODE_MAX_LENGTH_USA && input.matches(postCodeUSRegEx)
            Country.CA -> input.length in POSTAL_CODE_MIN_LENGTH_CA..POSTAL_CODE_MAX_LENGTH_CA && input.matches(postCodeCARegEx)
            Country.OTHER ->
                input.length in POSTAL_CODE_MIN_LENGTH_OTHER..POSTAL_CODE_MAX_LENGTH_OTHER &&
                    input.matches(
                        postCodeOtherRegEx,
                    )
            else -> false
        }
    }
}
