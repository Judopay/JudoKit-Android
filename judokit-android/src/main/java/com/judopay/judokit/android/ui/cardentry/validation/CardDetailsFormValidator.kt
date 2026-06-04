package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CardHolderNameValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CardNumberValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CountryValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.ExpirationDateValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.PostcodeValidator
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.SecurityCodeValidator

class CardDetailsFormValidator(
    supportedNetworks: List<CardNetwork>,
) {
    private val cardNumberValidator = CardNumberValidator(supportedNetworks = supportedNetworks)
    private val securityCodeValidator = SecurityCodeValidator()
    private val postcodeValidator = PostcodeValidator()

    private val validators: List<Validator> =
        listOf(
            cardNumberValidator,
            CardHolderNameValidator(),
            ExpirationDateValidator(),
            securityCodeValidator,
            CountryValidator(),
            postcodeValidator,
        )

    var cardNetwork: CardNetwork? = null
        set(value) {
            field = value
            securityCodeValidator.cardNetwork = value
        }

    var country: AVSCountry = AVSCountry.OTHER
        set(value) {
            field = value
            postcodeValidator.country = value
        }

    fun validateField(
        fieldType: CardDetailsFieldType,
        value: String,
        event: FormFieldEvent,
    ): ValidationResult? = validators.firstOrNull { it.fieldType == fieldType.name }?.validate(value, event)
}
