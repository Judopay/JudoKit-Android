package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.cardNumberMaxLength
import com.judokit.android.model.notSupportedErrorMessageResId
import com.judokit.android.ui.cardentry.components.FormFieldEvent
import com.judokit.android.ui.cardentry.components.FormFieldType
import com.judokit.android.ui.common.isValidLuhnNumber
import com.judokit.android.withWhitespacesRemoved

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
