package com.judopay.judokit.android.ui.common

import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonParser
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.GooglePayConfiguration
import com.judopay.judokit.android.model.googlepay.GooglePayDeferredParameters
import com.judopay.judokit.android.model.googlepay.GooglePayEnvironment
import com.judopay.judokit.android.model.googlepay.GooglePayPriceStatus
import com.judopay.judokit.android.model.googlepay.GooglePayRecurrencePeriod
import com.judopay.judokit.android.model.googlepay.GooglePayRecurrencePeriodItem
import com.judopay.judokit.android.model.googlepay.GooglePayRecurringParameters
import com.judopay.judokit.android.toJSONString
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing Google Pay MIT payment data request mapping")
internal class GooglePayMitMappersTest {
    private val judo: Judo =
        mockk(relaxed = true) {
            every { amount } returns Amount("10.00", Currency.GBP)
            every { judoId } returns "100100100"
            every { supportedCardNetworks } returns arrayOf(CardNetwork.VISA)
        }

    @Test
    fun `Given no MIT parameters, then request uses standard transactionInfo`() {
        val request = baseConfiguration().build().toGooglePayPaymentDataRequest(judo)

        assertThat(request.transactionInfo).isNotNull()
        assertThat(request.deferredTransactionInfo).isNull()
        assertThat(request.recurringTransactionInfo).isNull()
        assertThat(request.transactionInfo?.totalPrice).isEqualTo("10.00")
        assertThat(request.transactionInfo?.currencyCode).isEqualTo("GBP")
    }

    @Test
    fun `Given deferred parameters, then request uses deferredTransactionInfo only`() {
        val deferred =
            GooglePayDeferredParameters(
                immediateTotalPrice = "0.00",
                billingDateTime = "2027-01-01T08:00:00Z",
                priceStatus = GooglePayPriceStatus.FINAL,
                price = "200.00",
                label = "Hotel Room Reservation",
            )

        val request =
            baseConfiguration()
                .setDeferredParameters(deferred)
                .build()
                .toGooglePayPaymentDataRequest(judo)

        assertThat(request.transactionInfo).isNull()
        assertThat(request.recurringTransactionInfo).isNull()
        assertThat(request.deferredTransactionInfo).isNotNull()
        assertThat(request.deferredTransactionInfo?.label).isEqualTo("Hotel Room Reservation")
        assertThat(request.deferredTransactionInfo?.billingDateTime).isEqualTo("2027-01-01T08:00:00Z")
        assertThat(request.deferredTransactionInfo?.currencyCode).isEqualTo("GBP")
        assertThat(request.deferredTransactionInfo?.countryCode).isEqualTo("GB")
    }

    @Test
    fun `Given recurring parameters, then request uses recurringTransactionInfo only`() {
        val recurring =
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

        val request =
            baseConfiguration()
                .setRecurringParameters(recurring)
                .build()
                .toGooglePayPaymentDataRequest(judo)

        assertThat(request.transactionInfo).isNull()
        assertThat(request.deferredTransactionInfo).isNull()
        assertThat(request.recurringTransactionInfo).isNotNull()
        assertThat(request.recurringTransactionInfo?.immediateTotalPrice).isEqualTo("25.00")
        assertThat(request.recurringTransactionInfo?.recurrenceItems).hasSize(1)
        val recurrencePeriod =
            request.recurringTransactionInfo
                ?.recurrenceItems
                ?.first()
                ?.recurrencePeriod
        assertThat(recurrencePeriod).isEqualTo(GooglePayRecurrencePeriod.MONTH)
    }

    @Test
    fun `Given deferred parameters, then JSON contains deferredTransactionInfo and omits transactionInfo`() {
        val json =
            baseConfiguration()
                .setDeferredParameters(
                    GooglePayDeferredParameters(
                        immediateTotalPrice = "0.00",
                        billingDateTime = "2027-01-01T08:00:00Z",
                        priceStatus = GooglePayPriceStatus.FINAL,
                        price = "200.00",
                        label = "Hotel Room Reservation",
                    ),
                ).build()
                .toGooglePayPaymentDataRequest(judo)
                .toJSONString()

        val root = JsonParser.parseString(json).asJsonObject
        assertThat(root.has("deferredTransactionInfo")).isTrue()
        assertThat(root.has("transactionInfo")).isFalse()
        assertThat(root.has("recurringTransactionInfo")).isFalse()
    }

    @Test
    fun `Given recurring parameters, then JSON contains recurringTransactionInfo and omits transactionInfo`() {
        val json =
            baseConfiguration()
                .setRecurringParameters(
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
                    ),
                ).build()
                .toGooglePayPaymentDataRequest(judo)
                .toJSONString()

        val root = JsonParser.parseString(json).asJsonObject
        assertThat(root.has("recurringTransactionInfo")).isTrue()
        assertThat(root.has("transactionInfo")).isFalse()
        assertThat(root.has("deferredTransactionInfo")).isFalse()
    }

    private fun baseConfiguration(): GooglePayConfiguration.Builder =
        GooglePayConfiguration
            .Builder()
            .setEnvironment(GooglePayEnvironment.TEST)
            .setTransactionCountryCode("GB")
            .setMerchantName("Example Merchant")
}
