package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.cardNumberMaxLength
import com.judopay.model.notSupportedErrorMessageResId
import com.judopay.ui.cardentry.components.FormFieldEvent
import com.judopay.ui.cardentry.components.FormFieldType
import com.judopay.ui.common.isValidLuhnNumber
import com.judopay.withWhitespacesRemoved

data class CardNumberValidator(
    override val fieldType: FormFieldType = FormFieldType.NUMBER,
    var supportedNetworks: List<CardNetwork>
) : Validator {

    override fun validate(input: String, formFieldEvent: FormFieldEvent): ValidationResult {
        val number = input.withWhitespacesRemoved

        val network = CardNetwork.ofNumber(number)

        val isSupported = supportedNetworks.contains(network)
        val isValidLength = number.length == network.cardNumberMaxLength
        val isValid = isValidLuhnNumber(number) && isValidLength

        val message = if (isValidLength && network == CardNetwork.OTHER) {
            R.string.error_unknown_not_supported
        } else if (isSupported && !isValid && isValidLength) {
            R.string.check_card_number
        } else if (!isSupported) {
            network.notSupportedErrorMessageResId
        } else {
            R.string.empty
        }

        return ValidationResult(isSupported && isValid, message)
    }
}
