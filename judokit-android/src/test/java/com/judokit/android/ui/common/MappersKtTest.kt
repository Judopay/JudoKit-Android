package com.judokit.android.ui.common

import com.judokit.android.Judo
import com.judokit.android.model.GooglePayConfiguration
import com.judokit.android.model.googlepay.GPayPaymentGatewayParameters
import com.judokit.android.model.googlepay.GooglePayAuthMethod
import com.judokit.android.model.googlepay.GooglePayCardParameters
import com.judokit.android.model.googlepay.GooglePayPaymentMethod
import com.judokit.android.model.googlepay.GooglePayPaymentMethodTokenizationSpecification
import com.judokit.android.model.googlepay.GooglePayPaymentMethodType
import com.judokit.android.model.googlepay.GooglePayTokenizationSpecificationType
import com.judokit.android.model.isSupportedByGooglePay
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing com.judokit.android.ui.common.Mappers")
internal class MappersKtTest {

    @DisplayName("Given toGooglePayPaymentMethod, then map to GooglePayPaymentMethod")
    @Test
    fun mapToGooglePayPaymentMethod() {
        val googlePayConfiguration: GooglePayConfiguration = mockk(relaxed = true)
        val judo: Judo = mockk(relaxed = true)

        val networks = judo.supportedCardNetworks.filter { it.isSupportedByGooglePay }

        val cardParameters = GooglePayCardParameters(
            allowedAuthMethods = arrayOf(
                GooglePayAuthMethod.PAN_ONLY,
                GooglePayAuthMethod.CRYPTOGRAM_3DS
            ),
            allowedCardNetworks = networks.toTypedArray(),
            allowPrepaidCards = true,
            billingAddressRequired = googlePayConfiguration.isBillingAddressRequired,
            billingAddressParameters = googlePayConfiguration.billingAddressParameters
        )

        val tokenizationSpecification = GooglePayPaymentMethodTokenizationSpecification(
            type = GooglePayTokenizationSpecificationType.PAYMENT_GATEWAY,
            parameters = GPayPaymentGatewayParameters(gatewayMerchantId = judo.judoId)
        )

        val expected = GooglePayPaymentMethod(
            type = GooglePayPaymentMethodType.CARD,
            parameters = cardParameters,
            tokenizationSpecification = tokenizationSpecification
        )
        val actual = googlePayConfiguration.toGooglePayPaymentMethod(judo)

        assertEquals(GooglePayPaymentMethodType.CARD, actual.type)
    }
}