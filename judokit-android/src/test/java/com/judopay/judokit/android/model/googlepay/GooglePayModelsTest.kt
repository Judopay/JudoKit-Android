package com.judopay.judokit.android.model.googlepay

import com.judopay.judokit.android.model.CardNetwork
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing GooglePay model classes")
internal class GooglePayModelsTest {
    @Test
    @DisplayName("GooglePayAddress can be created with all fields")
    fun createGooglePayAddress() {
        val address =
            GooglePayAddress(
                name = "John Doe",
                postalCode = "SW1A 2AA",
                countryCode = "GB",
                phoneNumber = "+441234567890",
                address1 = "10 Downing Street",
                address2 = "Westminster",
                address3 = null,
                locality = "London",
                administrativeArea = "England",
                sortingCode = null,
            )
        assertEquals("John Doe", address.name)
        assertEquals("GB", address.countryCode)
        assertNull(address.address3)
    }

    @Test
    @DisplayName("GooglePayBillingAddressParameters can be created")
    fun createGooglePayBillingAddressParameters() {
        val params =
            GooglePayBillingAddressParameters(
                format = GooglePayAddressFormat.MIN,
                phoneNumberRequired = true,
            )
        assertEquals(GooglePayAddressFormat.MIN, params.format)
        assertEquals(true, params.phoneNumberRequired)
    }

    @Test
    @DisplayName("GooglePayCardParameters can be created")
    fun createGooglePayCardParameters() {
        val params =
            GooglePayCardParameters(
                allowedAuthMethods = arrayOf(GooglePayAuthMethod.PAN_ONLY),
                allowedCardNetworks = arrayOf(CardNetwork.VISA),
                allowPrepaidCards = true,
                allowCreditCards = false,
                billingAddressRequired = true,
                billingAddressParameters = null,
            )
        assertEquals(1, params.allowedAuthMethods.size)
        assertEquals(GooglePayAuthMethod.PAN_ONLY, params.allowedAuthMethods[0])
    }

    @Test
    @DisplayName("GooglePayTransactionInfo can be created")
    fun createGooglePayTransactionInfo() {
        val info =
            GooglePayTransactionInfo(
                currencyCode = "GBP",
                countryCode = "GB",
                transactionId = "txn-001",
                totalPriceStatus = GooglePayPriceStatus.FINAL,
                totalPrice = "10.00",
                totalPriceLabel = "Total",
                checkoutOption = GooglePayCheckoutOption.DEFAULT,
            )
        assertEquals("GBP", info.currencyCode)
        assertEquals(GooglePayPriceStatus.FINAL, info.totalPriceStatus)
    }

    @Test
    @DisplayName("GooglePayMerchantInfo can be created")
    fun createGooglePayMerchantInfo() {
        val info = GooglePayMerchantInfo(merchantName = "Test Merchant")
        assertEquals("Test Merchant", info.merchantName)
    }

    @Test
    @DisplayName("GPayPaymentGatewayParameters uses default gateway name 'judopay'")
    fun gPayPaymentGatewayParametersDefaultGateway() {
        val params = GPayPaymentGatewayParameters(gatewayMerchantId = "merchant-001")
        assertEquals("judopay", params.gateway)
        assertEquals("merchant-001", params.gatewayMerchantId)
    }

    @Test
    @DisplayName("GPayPaymentGatewayParameters custom gateway overrides default")
    fun gPayPaymentGatewayParametersCustomGateway() {
        val params = GPayPaymentGatewayParameters(gateway = "custom", gatewayMerchantId = "id-123")
        assertEquals("custom", params.gateway)
    }

    @Test
    @DisplayName("All GooglePayEnvironment enum values are accessible")
    fun googlePayEnvironmentValues() {
        assertEquals(2, GooglePayEnvironment.values().size)
        assertEquals(GooglePayEnvironment.PRODUCTION, GooglePayEnvironment.valueOf("PRODUCTION"))
        assertEquals(GooglePayEnvironment.TEST, GooglePayEnvironment.valueOf("TEST"))
    }

    @Test
    @DisplayName("All GooglePayPriceStatus enum values are accessible")
    fun googlePayPriceStatusValues() {
        assertEquals(3, GooglePayPriceStatus.values().size)
    }

    @Test
    @DisplayName("All GooglePayAuthMethod enum values are accessible")
    fun googlePayAuthMethodValues() {
        assertEquals(2, GooglePayAuthMethod.values().size)
    }

    @Test
    @DisplayName("All GooglePayAddressFormat enum values are accessible")
    fun googlePayAddressFormatValues() {
        assertEquals(2, GooglePayAddressFormat.values().size)
    }

    @Test
    @DisplayName("All GooglePayCheckoutOption enum values are accessible")
    fun googlePayCheckoutOptionValues() {
        assertEquals(2, GooglePayCheckoutOption.values().size)
    }

    @Test
    @DisplayName("All GooglePayPaymentMethodType enum values are accessible")
    fun googlePayPaymentMethodTypeValues() {
        assertEquals(1, GooglePayPaymentMethodType.values().size)
        assertEquals(GooglePayPaymentMethodType.CARD, GooglePayPaymentMethodType.valueOf("CARD"))
    }

    @Test
    @DisplayName("All GooglePayTokenizationSpecificationType enum values are accessible")
    fun googlePayTokenizationSpecificationTypeValues() {
        assertEquals(2, GooglePayTokenizationSpecificationType.values().size)
    }

    @Test
    @DisplayName("GooglePayCardInfo can be created with all fields")
    fun createGooglePayCardInfo() {
        val address =
            GooglePayAddress(
                name = "Jane",
                postalCode = "EC1A",
                countryCode = "GB",
                phoneNumber = "+441234567890",
                address1 = null,
                address2 = null,
                address3 = null,
                locality = null,
                administrativeArea = null,
                sortingCode = null,
            )
        val info =
            GooglePayCardInfo(
                cardDetails = "1234",
                cardNetwork = "VISA",
                billingAddress = address,
            )
        assertEquals("1234", info.cardDetails)
        assertEquals("VISA", info.cardNetwork)
        assertEquals("GB", info.billingAddress?.countryCode)
    }

    @Test
    @DisplayName("GooglePayPaymentMethodTokenizationData can be created")
    fun createGooglePayPaymentMethodTokenizationData() {
        val data = GooglePayPaymentMethodTokenizationData(type = "PAYMENT_GATEWAY", token = "tok_123")
        assertEquals("PAYMENT_GATEWAY", data.type)
        assertEquals("tok_123", data.token)
    }

    @Test
    @DisplayName("GooglePayPaymentMethodTokenizationData with null token can be created")
    fun createGooglePayPaymentMethodTokenizationDataNullToken() {
        val data = GooglePayPaymentMethodTokenizationData(type = "PAYMENT_GATEWAY", token = null)
        assertEquals(null, data.token)
    }

    @Test
    @DisplayName("GooglePayPaymentMethodTokenizationSpecification can be created")
    fun createGooglePayPaymentMethodTokenizationSpecification() {
        val params = GPayPaymentGatewayParameters(gatewayMerchantId = "merch-001")
        val spec =
            GooglePayPaymentMethodTokenizationSpecification(
                type = GooglePayTokenizationSpecificationType.PAYMENT_GATEWAY,
                parameters = params,
            )
        assertEquals(GooglePayTokenizationSpecificationType.PAYMENT_GATEWAY, spec.type)
        assertEquals("merch-001", spec.parameters.gatewayMerchantId)
    }

    @Test
    @DisplayName("GooglePayPaymentMethod can be created")
    fun createGooglePayPaymentMethod() {
        val params =
            GooglePayCardParameters(
                allowedAuthMethods = arrayOf(GooglePayAuthMethod.PAN_ONLY),
                allowedCardNetworks = emptyArray(),
                allowPrepaidCards = null,
                allowCreditCards = null,
                billingAddressRequired = null,
                billingAddressParameters = null,
            )
        val method =
            GooglePayPaymentMethod(
                type = GooglePayPaymentMethodType.CARD,
                parameters = params,
                tokenizationSpecification = null,
            )
        assertEquals(GooglePayPaymentMethodType.CARD, method.type)
        assertEquals(null, method.tokenizationSpecification)
    }

    @Test
    @DisplayName("GooglePayPaymentMethodData can be created")
    fun createGooglePayPaymentMethodData() {
        val cardInfo = GooglePayCardInfo(cardDetails = "9876", cardNetwork = "MASTERCARD", billingAddress = null)
        val tokenData = GooglePayPaymentMethodTokenizationData(type = "PAYMENT_GATEWAY", token = null)
        val data =
            GooglePayPaymentMethodData(
                type = "CARD",
                description = "Visa •••• 1234",
                info = cardInfo,
                tokenizationData = tokenData,
            )
        assertEquals("CARD", data.type)
        assertEquals("MASTERCARD", data.info.cardNetwork)
    }

    @Test
    @DisplayName("GooglePayIsReadyToPayRequest can be created")
    fun createGooglePayIsReadyToPayRequest() {
        val params =
            GooglePayCardParameters(
                allowedAuthMethods = arrayOf(GooglePayAuthMethod.PAN_ONLY),
                allowedCardNetworks = emptyArray(),
                allowPrepaidCards = null,
                allowCreditCards = null,
                billingAddressRequired = null,
                billingAddressParameters = null,
            )
        val method =
            GooglePayPaymentMethod(
                type = GooglePayPaymentMethodType.CARD,
                parameters = params,
                tokenizationSpecification = null,
            )
        val request =
            GooglePayIsReadyToPayRequest(
                apiVersion = 2,
                apiVersionMinor = 0,
                allowedPaymentMethods = arrayOf(method),
                existingPaymentMethodRequired = true,
            )
        assertEquals(2, request.apiVersion)
        assertEquals(0, request.apiVersionMinor)
        assertEquals(true, request.existingPaymentMethodRequired)
    }

    @Test
    @DisplayName("GooglePayPaymentData can be created")
    fun createGooglePayPaymentData() {
        val address =
            GooglePayAddress(
                name = "John",
                postalCode = "10001",
                countryCode = "US",
                phoneNumber = "+12025551234",
                address1 = null,
                address2 = null,
                address3 = null,
                locality = null,
                administrativeArea = null,
                sortingCode = null,
            )
        val cardInfo = GooglePayCardInfo(cardDetails = "5678", cardNetwork = "DISCOVER", billingAddress = null)
        val tokenData = GooglePayPaymentMethodTokenizationData(type = "PAYMENT_GATEWAY", token = "tok_pay")
        val methodData =
            GooglePayPaymentMethodData(
                type = "CARD",
                description = "desc",
                info = cardInfo,
                tokenizationData = tokenData,
            )
        val paymentData =
            GooglePayPaymentData(
                apiVersion = 2,
                apiVersionMinor = 0,
                paymentMethodData = methodData,
                email = "user@example.com",
                shippingAddress = address,
            )
        assertEquals(2, paymentData.apiVersion)
        assertEquals("user@example.com", paymentData.email)
        assertEquals("US", paymentData.shippingAddress?.countryCode)
    }

    @Test
    @DisplayName("GooglePayShippingAddressParameters can be created")
    fun createGooglePayShippingAddressParameters() {
        val params =
            GooglePayShippingAddressParameters(
                allowedCountryCodes = arrayOf("GB", "US"),
                phoneNumberRequired = true,
            )
        assertEquals(2, params.allowedCountryCodes?.size)
        assertEquals(true, params.phoneNumberRequired)
    }

    @Test
    @DisplayName("GooglePayPaymentDataRequest can be created")
    fun createGooglePayPaymentDataRequest() {
        val cardParams =
            GooglePayCardParameters(
                allowedAuthMethods = arrayOf(GooglePayAuthMethod.PAN_ONLY),
                allowedCardNetworks = emptyArray(),
                allowPrepaidCards = null,
                allowCreditCards = null,
                billingAddressRequired = null,
                billingAddressParameters = null,
            )
        val method =
            GooglePayPaymentMethod(
                type = GooglePayPaymentMethodType.CARD,
                parameters = cardParams,
                tokenizationSpecification = null,
            )
        val txnInfo =
            GooglePayTransactionInfo(
                currencyCode = "USD",
                countryCode = "US",
                transactionId = "txn-001",
                totalPriceStatus = GooglePayPriceStatus.FINAL,
                totalPrice = "5.00",
                totalPriceLabel = null,
                checkoutOption = null,
            )
        val request =
            GooglePayPaymentDataRequest(
                apiVersion = 2,
                apiVersionMinor = 0,
                merchantInfo = GooglePayMerchantInfo("Test"),
                allowedPaymentMethods = arrayOf(method),
                transactionInfo = txnInfo,
                emailRequired = false,
                shippingAddressRequired = null,
                shippingAddressParameters = null,
            )
        assertEquals(2, request.apiVersion)
        assertEquals("Test", request.merchantInfo?.merchantName)
    }

    @Test
    @DisplayName("GooglePayAddress inequality when countryCode differs")
    fun googlePayAddressInequality() {
        val a1 = GooglePayAddress("John", "SW1", "GB", "0800", null, null, null, null, null, null)
        val a2 = GooglePayAddress("John", "SW1", "US", "0800", null, null, null, null, null, null)
        assertNotEquals(a1, a2)
        assertNotEquals(a1.hashCode(), a2.hashCode())
    }

    @Test
    @DisplayName("GooglePayBillingAddressParameters inequality when format differs")
    fun googlePayBillingAddressParametersInequality() {
        val p1 = GooglePayBillingAddressParameters(format = GooglePayAddressFormat.MIN, phoneNumberRequired = false)
        val p2 = GooglePayBillingAddressParameters(format = GooglePayAddressFormat.FULL, phoneNumberRequired = false)
        assertNotEquals(p1, p2)
    }

    @Test
    @DisplayName("GooglePayMerchantInfo inequality when merchantName differs")
    fun googlePayMerchantInfoInequality() {
        assertNotEquals(GooglePayMerchantInfo("Merchant A"), GooglePayMerchantInfo("Merchant B"))
    }

    @Test
    @DisplayName("GPayPaymentGatewayParameters inequality when gatewayMerchantId differs")
    fun gPayPaymentGatewayParametersInequality() {
        assertNotEquals(
            GPayPaymentGatewayParameters(gatewayMerchantId = "id-1"),
            GPayPaymentGatewayParameters(gatewayMerchantId = "id-2"),
        )
    }

    @Test
    @DisplayName("GooglePayCardInfo inequality when cardNetwork differs")
    fun googlePayCardInfoInequality() {
        val a1 = GooglePayCardInfo("1234", "VISA", null)
        val a2 = GooglePayCardInfo("1234", "MASTERCARD", null)
        assertNotEquals(a1, a2)
    }

    @Test
    @DisplayName("GooglePayTransactionInfo inequality when currencyCode differs")
    fun googlePayTransactionInfoInequality() {
        val t1 = GooglePayTransactionInfo("GBP", "GB", null, GooglePayPriceStatus.FINAL, "10.00", null, null)
        val t2 = GooglePayTransactionInfo("USD", "GB", null, GooglePayPriceStatus.FINAL, "10.00", null, null)
        assertNotEquals(t1, t2)
    }

    @Test
    @DisplayName("GooglePayPaymentMethodTokenizationData inequality when type differs")
    fun googlePayPaymentMethodTokenizationDataInequality() {
        assertNotEquals(
            GooglePayPaymentMethodTokenizationData("PAYMENT_GATEWAY", null),
            GooglePayPaymentMethodTokenizationData("DIRECT", null),
        )
    }

    @Test
    @DisplayName("GooglePayShippingAddressParameters inequality when phoneNumberRequired differs")
    fun googlePayShippingAddressParametersInequality() {
        assertNotEquals(
            GooglePayShippingAddressParameters(phoneNumberRequired = true),
            GooglePayShippingAddressParameters(phoneNumberRequired = false),
        )
    }
}
