package com.judopay.judokit.android.model

import com.google.common.truth.Truth.assertThat
import com.judopay.judokit.android.model.googlepay.GooglePayAddressFormat
import com.judopay.judokit.android.model.googlepay.GooglePayBillingAddressParameters
import com.judopay.judokit.android.model.googlepay.GooglePayCheckoutOption
import com.judopay.judokit.android.model.googlepay.GooglePayDeferredParameters
import com.judopay.judokit.android.model.googlepay.GooglePayEnvironment
import com.judopay.judokit.android.model.googlepay.GooglePayPriceStatus
import com.judopay.judokit.android.model.googlepay.GooglePayRecurrencePeriod
import com.judopay.judokit.android.model.googlepay.GooglePayRecurrencePeriodItem
import com.judopay.judokit.android.model.googlepay.GooglePayRecurringParameters
import com.judopay.judokit.android.model.googlepay.GooglePayShippingAddressParameters
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
        val shippingAddressParams =
            GooglePayShippingAddressParameters(
                allowedCountryCodes = arrayOf("US"),
                phoneNumberRequired = true,
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
        val shippingAddressParams =
            GooglePayShippingAddressParameters(
                allowedCountryCodes = arrayOf("USA"),
                phoneNumberRequired = true,
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
        val actualTotalPriceStatus =
            googlePayConfigurationBuilder
                .setEnvironment(GooglePayEnvironment.TEST)
                .setTransactionCountryCode("US")
                .build()
                .totalPriceStatus
        val expectedTotalPriceStatus = GooglePayPriceStatus.FINAL

        assertEquals(expectedTotalPriceStatus, actualTotalPriceStatus)
    }

    @Test
    fun `Given allowPrepaidCards is set, then build() should use this value`() {
        val sut =
            googlePayConfigurationBuilder
                .setEnvironment(GooglePayEnvironment.TEST)
                .setTransactionCountryCode("US")
        assertThat(sut.setAllowPrepaidCards(true).build().allowPrepaidCards).isTrue()
        assertThat(sut.setAllowPrepaidCards(false).build().allowPrepaidCards).isFalse()
        assertThat(sut.setAllowPrepaidCards(null).build().allowPrepaidCards).isNull()
    }

    @Test
    fun `Given allowCreditCards is set, then build() should use this value`() {
        val sut =
            googlePayConfigurationBuilder
                .setEnvironment(GooglePayEnvironment.TEST)
                .setTransactionCountryCode("US")
        assertThat(sut.setAllowCreditCards(true).build().allowCreditCards).isTrue()
        assertThat(sut.setAllowCreditCards(false).build().allowCreditCards).isFalse()
        assertThat(sut.setAllowCreditCards(null).build().allowCreditCards).isNull()
    }

    @Test
    fun `Given deferredParameters are set, then build() keeps them`() {
        val parameters =
            GooglePayDeferredParameters(
                immediateTotalPrice = "0.00",
                billingDateTime = "2027-01-01T08:00:00Z",
                priceStatus = GooglePayPriceStatus.FINAL,
                price = "200.00",
                label = "Hotel Room Reservation",
            )

        val configuration =
            googlePayConfigurationBuilder
                .setEnvironment(GooglePayEnvironment.TEST)
                .setTransactionCountryCode("US")
                .setDeferredParameters(parameters)
                .build()

        assertEquals(parameters, configuration.deferredParameters)
        assertThat(configuration.recurringParameters).isNull()
    }

    @Test
    fun `Given recurringParameters are set, then build() keeps them`() {
        val parameters =
            GooglePayRecurringParameters(
                immediateTotalPrice = "25.00",
                recurrenceItems =
                    listOf(
                        GooglePayRecurrencePeriodItem(
                            label = "Premium Plan Monthly Subscription",
                            price = "25.00",
                            priceStatus = GooglePayPriceStatus.FINAL,
                            recurrencePeriod = GooglePayRecurrencePeriod.MONTH,
                            recurrencePeriodCount = 1,
                        ),
                    ),
            )

        val configuration =
            googlePayConfigurationBuilder
                .setEnvironment(GooglePayEnvironment.TEST)
                .setTransactionCountryCode("US")
                .setRecurringParameters(parameters)
                .build()

        assertEquals(parameters, configuration.recurringParameters)
        assertThat(configuration.deferredParameters).isNull()
    }
}
