package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.REG_EX_CARDHOLDER_NAME

private const val MIN_CARDHOLDER_NAME_LENGTH = 4

data class CardHolderNameValidator(override val fieldType: String = CardDetailsFieldType.HOLDER_NAME.name) :
    Validator {
    private val cardholderNameRegEx = REG_EX_CARDHOLDER_NAME.toRegex()

    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val shouldNotDisplayMessage = formFieldEvent != FormFieldEvent.FOCUS_CHANGED

        val isBlank = input.isBlank()
        val isTooShort = input.length < MIN_CARDHOLDER_NAME_LENGTH
        val isValidCharactersSet = cardholderNameRegEx.matches(input)
        val isNotValid = isBlank || isTooShort || !isValidCharactersSet

        val message =
            when {
                shouldNotDisplayMessage -> R.string.jp_empty
                isBlank -> R.string.jp_card_holder_name_required
                isTooShort -> R.string.jp_card_holder_name_too_short
                !isValidCharactersSet -> R.string.jp_card_holder_name_special_chars
                else -> R.string.jp_empty
            }

        return ValidationResult(!isNotValid, message)
    }
}
