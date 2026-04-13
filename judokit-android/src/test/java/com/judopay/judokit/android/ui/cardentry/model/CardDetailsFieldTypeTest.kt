package com.judopay.judokit.android.ui.cardentry.model

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardDetailsFieldType and BillingDetailsFieldType extensions")
internal class CardDetailsFieldTypeTest {
    @Test
    @DisplayName("CardDetailsFieldType.fieldHintResId returns a value for NUMBER")
    fun fieldHintResIdForNumber() {
        assertNotNull(CardDetailsFieldType.NUMBER.fieldHintResId)
    }

    @Test
    @DisplayName("CardDetailsFieldType.fieldHintResId returns a value for each type")
    fun fieldHintResIdForAllCardTypes() {
        CardDetailsFieldType.values().forEach { type ->
            assertNotNull(type.fieldHintResId)
        }
    }

    @Test
    @DisplayName("BillingDetailsFieldType.fieldHintResId returns a value for each type")
    fun fieldHintResIdForAllBillingTypes() {
        BillingDetailsFieldType.values().forEach { type ->
            assertNotNull(type.fieldHintResId)
        }
    }

    @Test
    @DisplayName("BillingDetailsFieldType has expected values")
    fun billingDetailsFieldTypeValues() {
        val values = BillingDetailsFieldType.values()
        assert(values.contains(BillingDetailsFieldType.EMAIL))
        assert(values.contains(BillingDetailsFieldType.COUNTRY))
        assert(values.contains(BillingDetailsFieldType.MOBILE_NUMBER))
        assert(values.contains(BillingDetailsFieldType.CITY))
        assert(values.contains(BillingDetailsFieldType.POST_CODE))
    }

    @Test
    @DisplayName("valueOfFieldWithType returns cardNumber for NUMBER type")
    fun valueOfFieldWithTypeReturnsCardNumber() {
        val model = CardDetailsInputModel(cardNumber = "4111111111111111")
        val value = model.valueOfFieldWithType(CardDetailsFieldType.NUMBER)
        assert(value == "4111111111111111")
    }

    @Test
    @DisplayName("valueOfFieldWithType returns correct value for each field type")
    fun valueOfFieldWithTypeForAllTypes() {
        val model =
            CardDetailsInputModel(
                cardNumber = "4111",
                cardHolderName = "John",
                expirationDate = "12/25",
                securityNumber = "123",
                country = "United Kingdom",
                postCode = "SW1A",
            )
        assert(model.valueOfFieldWithType(CardDetailsFieldType.NUMBER) == "4111")
        assert(model.valueOfFieldWithType(CardDetailsFieldType.HOLDER_NAME) == "John")
        assert(model.valueOfFieldWithType(CardDetailsFieldType.EXPIRATION_DATE) == "12/25")
        assert(model.valueOfFieldWithType(CardDetailsFieldType.SECURITY_NUMBER) == "123")
        assert(model.valueOfFieldWithType(CardDetailsFieldType.COUNTRY) == "United Kingdom")
        assert(model.valueOfFieldWithType(CardDetailsFieldType.POST_CODE) == "SW1A")
    }

    @Test
    @DisplayName("valueOfBillingDetailsFieldWithType returns correct value for each field type")
    fun valueOfBillingDetailsFieldWithTypeForAllTypes() {
        val model =
            BillingDetailsInputModel(
                email = "test@test.com",
                countryCode = "826",
                administrativeDivision = "CA",
                postalCode = "SW1A",
                phoneCountryCode = "+44",
                mobileNumber = "07700900000",
                addressLine1 = "Line 1",
                addressLine2 = "Line 2",
                addressLine3 = "Line 3",
                city = "London",
            )
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.EMAIL) == "test@test.com")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.COUNTRY) == "826")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.ADMINISTRATIVE_DIVISION) == "CA")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.POST_CODE) == "SW1A")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.PHONE_COUNTRY_CODE) == "+44")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.MOBILE_NUMBER) == "07700900000")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.ADDRESS_LINE_1) == "Line 1")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.ADDRESS_LINE_2) == "Line 2")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.ADDRESS_LINE_3) == "Line 3")
        assert(model.valueOfBillingDetailsFieldWithType(BillingDetailsFieldType.CITY) == "London")
    }
}
