package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.model.Country
import com.judokit.android.ui.cardentry.components.FormFieldEvent
import com.judokit.android.ui.cardentry.components.FormFieldType

data class PostcodeValidator(
    var country: Country? = null,
    override val fieldType: FormFieldType = FormFieldType.POST_CODE
) : Validator {

    private val regExGBPostcode =
        "(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX‌​]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY]))))\\s?[0-9][A-Z-[C‌​IKMOV]]{2})".toRegex()
    private val regExUSPostcode = "(^\\d{5}$)|(^\\d{5}-\\d{4}$)".toRegex()
    private val regExCAPostcode =
        "[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]".toRegex()

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val isValid = isPostCodeValid(input)
        val message = if (isValid) R.string.empty else errorString()
        return ValidationResult(isValid, message)
    }

    private fun errorString(): Int = when (country) {
        Country.GB,
        Country.CA -> R.string.invalid_postcode
        Country.US -> R.string.invalid_zip_code
        else -> R.string.empty
    }

    private fun isPostCodeValid(input: String): Boolean = when (country) {
        Country.GB -> input.matches(regExGBPostcode)
        Country.US -> input.matches(regExUSPostcode)
        Country.CA -> input.matches(regExCAPostcode)
        Country.OTHER -> input.isNotBlank()
        else -> false
    }
}
