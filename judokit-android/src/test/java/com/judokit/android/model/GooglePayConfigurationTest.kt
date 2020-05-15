package com.judokit.android.model

import com.judokit.android.model.googlepay.GooglePayEnvironment
import com.judokit.android.model.googlepay.GooglePayShippingAddressParameters
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing the GooglePay configuration object builder logic")
internal class GooglePayConfigurationTest {

    lateinit var googlePayConfigurationBuilder: GooglePayConfiguration.Builder

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
}
