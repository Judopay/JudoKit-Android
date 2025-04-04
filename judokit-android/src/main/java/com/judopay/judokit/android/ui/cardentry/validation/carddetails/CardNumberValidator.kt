package com.judopay.judokit.android.ui.cardentry.validation.carddetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.cardNumberMaxLength
import com.judopay.judokit.android.model.notSupportedErrorMessageResId
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.cardentry.validation.Validator
import com.judopay.judokit.android.ui.common.isValidLuhnNumber
import com.judopay.judokit.android.withWhitespacesRemoved

data class CardNumberValidator(
    override val fieldType: String = CardDetailsFieldType.NUMBER.name,
    var supportedNetworks: List<CardNetwork>,
) : Validator {
    override fun validate(
        input: String,
        formFieldEvent: FormFieldEvent,
    ): ValidationResult {
        val number = input.withWhitespacesRemoved

        val network = CardNetwork.ofNumber(number)

        val isSupported = supportedNetworks.contains(network)
        val isValidLength = number.length == network.cardNumberMaxLength
        val isValid = isValidLuhnNumber(number) && isValidLength
        val shouldNotDisplayMessage = formFieldEvent != FormFieldEvent.FOCUS_CHANGED

        val message =
            when {
                shouldNotDisplayMessage -> R.string.jp_empty
                isValidLength && network == CardNetwork.OTHER -> R.string.jp_error_unknown_not_supported
                isSupported && !isValid -> R.string.jp_check_card_number
                !isSupported -> network.notSupportedErrorMessageResId
                else -> R.string.jp_empty
            }

        return ValidationResult(isSupported && isValid, message)
    }
}
