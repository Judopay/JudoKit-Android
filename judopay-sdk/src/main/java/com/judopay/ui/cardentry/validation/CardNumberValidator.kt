package com.judopay.ui.cardentry.validation

import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.cardNumberMaxLength
import com.judopay.model.notSupportedErrorMessageResId
import com.judopay.ui.cardentry.components.FormFieldType
import com.judopay.ui.common.isValidLuhnNumber
import com.judopay.withWhitespacesRemoved

data class CardNumberValidator(
    override val fieldType: FormFieldType = FormFieldType.NUMBER,
    val supportedNetworks: List<CardNetwork>
) : Validator {
    override fun validate(input: String): ValidationResult {
        val number = input.withWhitespacesRemoved

        val network = CardNetwork.ofNumber(number)
                ?: return ValidationResult(false, R.string.check_card_number)

        val isSupported = supportedNetworks.contains(network)
        val isValidLength = number.length == network.cardNumberMaxLength
        val isValid = isValidLuhnNumber(number) && isValidLength

        val message = if (isSupported && !isValid) {
            R.string.check_card_number
        } else {
            network.notSupportedErrorMessageResId
        }

        return ValidationResult(isValid, message)
    }
}
