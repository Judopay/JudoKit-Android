package com.judokit.android.model

import com.judokit.android.model.googlepay.GooglePayAddressFormat
import com.judokit.android.model.googlepay.GooglePayBillingAddressParameters
import com.judokit.android.model.googlepay.GooglePayCheckoutOption
import com.judokit.android.model.googlepay.GooglePayEnvironment
import com.judokit.android.model.googlepay.GooglePayPriceStatus
import com.judokit.android.model.googlepay.GooglePayShippingAddressParameters
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing the GooglePay configuration object builder logic")
internal class GooglePayConfigurationTest {

    private lateinit var googlePayConfigurationBuilder: GooglePayConfiguration.Builder

    @BeforeEach
    fun setUp() {
        googlePayConfigurationBuilder = GooglePayConfiguration.Builder()
    }

    @Test
    @DisplayName("When no environment is specified, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsWhenNoEnvIsSpecified() {
        assertThrows<IllegalArgumentException> {
            googlePayConfigurationBuilder.build()
        }
    }

    @Test
    @DisplayName("Given transactionCountryCode is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsWhenTransactionCountryCodeNull() {
        assertThrows<IllegalArgumentException> {
            googlePayConfigurationBuilder
                .setTransactionCountryCode(null)
                .build()
        }
    }

    @Test
    @DisplayName("Given all required fields are specified, then build() should return GooglePayConfigurationObject")
    fun returnGooglePayConfigurationObjectWhenAllRequiredFieldsSpecified() {
        val billingAddressParameters =
            GooglePayBillingAddressParameters(GooglePayAddressFormat.MIN, false)
        val shippingAddressParams = GooglePayShippingAddressParameters(
            allowedCountryCodes = arrayOf("US"),
            phoneNumberRequired = true
        )

        assertDoesNotThrow {
            googlePayConfigurationBuilder
                .setMerchantName("Name")
                .setTransactionId("id")
                .setTotalPriceStatus(GooglePayPriceStatus.FINAL)
                .setCheckoutOption(GooglePayCheckoutOption.DEFAULT)
                .setIsEmailRequired(false)
                .setIsBillingAddressRequired(false)
                .setBillingAddressParameters(billingAddressParameters)
                .setShippingAddressParameters(shippingAddressParams)
                .setIsShippingAddressRequired(false)
                .setTotalPriceLabel("label")
                .setEnvironment(GooglePayEnvironment.TEST)
                .setTransactionCountryCode("US")
                .build()
        }
    }

    @Test
    @DisplayName("When a invalid allowed shipping country code is specified, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsWhenWrongShippingCountryCodeIsSpecified() {
        val shippingAddressParams = GooglePayShippingAddressParameters(
            allowedCountryCodes = arrayOf("USA"),
            phoneNumberRequired = true
        )

        assertThrows<IllegalArgumentException> {
            googlePayConfigurationBuilder
                .setTransactionCountryCode("US")
                .setEnvironment(GooglePayEnvironment.TEST)
                .setShippingAddressParameters(shippingAddressParams)
                .build()
        }
    }

    @Test
    @DisplayName("Given totalPriceStatus is null, then use FINAL default value")
    fun useFinalDefaultValueOnTotalPriceStatusNull() {
        val actualTotalPriceStatus = googlePayConfigurationBuilder
            .setEnvironment(GooglePayEnvironment.TEST)
            .setTransactionCountryCode("US")
            .build().totalPriceStatus
        val expectedTotalPriceStatus = GooglePayPriceStatus.FINAL

        assertEquals(expectedTotalPriceStatus, actualTotalPriceStatus)
    }
}
