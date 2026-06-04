package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Test CardDetailsFormValidator")
internal class CardDetailsFormValidatorTest {
    private lateinit var sut: CardDetailsFormValidator

    @BeforeEach
    fun setUp() {
        sut = CardDetailsFormValidator(supportedNetworks = listOf(CardNetwork.VISA, CardNetwork.MASTERCARD))
    }

    @Test
    @DisplayName("Given a valid card holder name, then validateField returns a valid result")
    fun validateFieldReturnsResultForHolderName() {
        val result = sut.validateField(CardDetailsFieldType.HOLDER_NAME, "Alice Smith", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
        assertTrue(result!!.isValid)
    }

    @Test
    @DisplayName("Given a card number field, then validateField returns a non-null result")
    fun validateFieldReturnsResultForNumber() {
        val result = sut.validateField(CardDetailsFieldType.NUMBER, "4111111111111111", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given an expiration date field with a future date, then validateField returns a non-null result")
    fun validateFieldReturnsResultForExpirationDate() {
        val result = sut.validateField(CardDetailsFieldType.EXPIRATION_DATE, "12/30", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given security number field, then validateField returns a non-null result")
    fun validateFieldReturnsResultForSecurityNumber() {
        val result = sut.validateField(CardDetailsFieldType.SECURITY_NUMBER, "452", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given country field with non-empty value, then validateField returns valid result")
    fun validateFieldReturnsValidForCountry() {
        val result = sut.validateField(CardDetailsFieldType.COUNTRY, "United Kingdom", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
        assertTrue(result!!.isValid)
    }

    @Test
    @DisplayName("Given post code field, then validateField returns a non-null result")
    fun validateFieldReturnsResultForPostCode() {
        val result = sut.validateField(CardDetailsFieldType.POST_CODE, "SW1A 1AA", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given cardNetwork is set to VISA, then security code validation uses VISA rules")
    fun settingCardNetworkAffectsSecurityCodeValidation() {
        sut.cardNetwork = CardNetwork.VISA
        val result = sut.validateField(CardDetailsFieldType.SECURITY_NUMBER, "123", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
        assertTrue(result!!.isValid)
    }

    @Test
    @DisplayName("Given cardNetwork is set to AMEX, then four-digit security code is valid")
    fun amexCardNetworkAcceptsFourDigitSecurityCode() {
        sut.cardNetwork = CardNetwork.AMEX
        val result = sut.validateField(CardDetailsFieldType.SECURITY_NUMBER, "1234", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
        assertTrue(result!!.isValid)
    }

    @Test
    @DisplayName("Given country is set to OTHER, then post code validator uses OTHER rules")
    fun settingCountryAffectsPostCodeValidation() {
        sut.country = AVSCountry.OTHER
        val result = sut.validateField(CardDetailsFieldType.POST_CODE, "12345", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given country is set to US, then post code validator uses US rules")
    fun settingCountryToUSAffectsPostCodeValidation() {
        sut.country = AVSCountry.US
        val result = sut.validateField(CardDetailsFieldType.POST_CODE, "12345", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
        assertTrue(result!!.isValid)
    }

    @Test
    @DisplayName("Given null cardNetwork, then security code validation still returns a result")
    fun nullCardNetworkReturnsResult() {
        sut.cardNetwork = null
        val result = sut.validateField(CardDetailsFieldType.SECURITY_NUMBER, "123", FormFieldEvent.TEXT_CHANGED)
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given cardNetwork property is read back after setting, then it reflects the assigned value")
    fun cardNetworkPropertyReflectsAssignedValue() {
        sut.cardNetwork = CardNetwork.AMEX
        assertEquals(CardNetwork.AMEX, sut.cardNetwork)
    }

    @Test
    @DisplayName("Given country property is read back after setting, then it reflects the assigned value")
    fun countryPropertyReflectsAssignedValue() {
        sut.country = AVSCountry.GB
        assertEquals(AVSCountry.GB, sut.country)
    }
}
