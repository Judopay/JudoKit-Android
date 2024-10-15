package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CardNumberValidator
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CardNumberValidatorTest {
    private val formModel: FormModel = mockk(relaxed = true)
    private lateinit var validator: CardNumberValidator

    @BeforeEach
    internal fun setUp() {
        every { formModel.cardDetailsInputModel.supportedNetworks } returns CardNetwork.values().toList()

        validator = CardNumberValidator(supportedNetworks = formModel.cardDetailsInputModel.supportedNetworks)
    }

    @Test
    @DisplayName(
        "Given that card number is unsupported, then a validation error should return with unknown card network not supported string",
    )
    fun unsupportedCardNumber() {
        assertEquals(
            validator.validate("1111 1111 1111 1111", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_error_unknown_not_supported),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that card number is supported with invalid length and invalid luhn number, then a validation error should return with empty string",
    )
    fun supportedAndInvalidCardNumber() {
        assertEquals(
            validator.validate("4111"),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that card number is unsupported with valid length and invalid luhn number, then a validation error should return with check card number string",
    )
    fun unsupportedAndInvalidLengthCardNumber() {
        assertEquals(
            validator.validate("4111 1111 1111 1112", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_check_card_number),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that card number is unsupported with invalid length and valid luhn number, then a validation error should return empty string",
    )
    fun unsupportedAndInvalidLuhnCardNumber() {
        assertEquals(
            validator.validate("4111 1111 1111 111"),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given that card number is unsupported with valid length and valid luhn number, then a validation error should return with error_visa_not_supported string",
    )
    fun unsupportedAndValidCardNumber() {
        every { formModel.cardDetailsInputModel.supportedNetworks } returns listOf(CardNetwork.AMEX)

        validator = CardNumberValidator(supportedNetworks = formModel.cardDetailsInputModel.supportedNetworks)

        assertEquals(
            validator.validate("4111 1111 1111 1111", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_error_visa_not_supported),
        )
    }

    @Test
    @DisplayName("Given that card number is supported with valid length and valid luhn number, then validation passes")
    fun supportedAndValidCardNumber() {
        assertEquals(
            validator.validate("4111 1111 1111 1111"),
            ValidationResult(true, R.string.jp_empty),
        )
    }
}
