package com.judokit.android.model

import com.judokit.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing PaymentWidgetType extension functions")
internal class PaymentWidgetTypeKtTest {
    @DisplayName("Given navigationGraphId is called, when widget type is CARD_PAYMENT, then return judo_card_input_graph resource id")
    @Test
    fun returnCardInputNavigationGraphOnCardPayment() {
        assertEquals(
            R.navigation.judo_card_input_graph,
            PaymentWidgetType.CARD_PAYMENT.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is PRE_AUTH, then return judo_card_input_graph resource id")
    @Test
    fun returnCardInputNavigationGraphOnPreAuth() {
        assertEquals(
            R.navigation.judo_card_input_graph,
            PaymentWidgetType.PRE_AUTH.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is REGISTER_CARD, then return judo_card_input_graph resource id")
    @Test
    fun returnCardInputNavigationGraphOnRegisterCard() {
        assertEquals(
            R.navigation.judo_card_input_graph,
            PaymentWidgetType.REGISTER_CARD.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is CREATE_CARD_TOKEN, then return judo_card_input_graph resource id")
    @Test
    fun returnCardInputNavigationGraphOnCreateCardToken() {
        assertEquals(
            R.navigation.judo_card_input_graph,
            PaymentWidgetType.CREATE_CARD_TOKEN.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is CHECK_CARD, then return judo_card_input_graph resource id")
    @Test
    fun returnCardInputNavigationGraphOnCheckCard() {
        assertEquals(
            R.navigation.judo_card_input_graph,
            PaymentWidgetType.CHECK_CARD.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is SERVER_TO_SERVER_PAYMENT_METHODS, then return judo_payment_methods_graph resource id")
    @Test
    fun returnPaymentMethodsNavigationGraphOnServerToServer() {
        assertEquals(
            R.navigation.judo_payment_methods_graph,
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is PAYMENT_METHODS, then return judo_payment_methods_graph resource id")
    @Test
    fun returnPaymentMethodsNavigationGraphOnPaymentMethods() {
        assertEquals(
            R.navigation.judo_payment_methods_graph,
            PaymentWidgetType.PAYMENT_METHODS.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is PRE_AUTH_PAYMENT_METHODS, then return judo_payment_methods_graph resource id")
    @Test
    fun returnPaymentMethodsNavigationGraphOnPreAuthPaymentMethods() {
        assertEquals(
            R.navigation.judo_payment_methods_graph,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is PAY_BY_BANK, then return judo_polling_status_graph resource id")
    @Test
    fun returnJudoPollingStatusGraphOnPayByBank() {
        assertEquals(
            R.navigation.judo_polling_status_graph,
            PaymentWidgetType.PAY_BY_BANK_APP.navigationGraphId
        )
    }

    @DisplayName("Given navigationGraphId is called, when widget type is GOOGLE_PAY, then throw UnsupportedOperationException")
    @Test
    fun throwExceptionOnGooglePay() {
        assertThrows<UnsupportedOperationException> {
            PaymentWidgetType.GOOGLE_PAY.navigationGraphId
        }
    }

    @DisplayName("Given isPaymentMethodsWidget is called, when widget type is PAYMENT_METHODS, then return true")
    @Test
    fun returnTrueOnIsPaymentMethodsWidgetCallWithPaymentMethodsType() {
        assertTrue(PaymentWidgetType.PAYMENT_METHODS.isPaymentMethodsWidget)
    }

    @DisplayName("Given isPaymentMethodsWidget is called, when widget type is PRE_AUTH_PAYMENT_METHODS, then return true")
    @Test
    fun returnTrueOnIsPaymentMethodsWidgetCallWithPreAuthPaymentMethodsType() {
        assertTrue(PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS.isPaymentMethodsWidget)
    }

    @DisplayName("Given isPaymentMethodsWidget is called, when widget type is SERVER_TO_SERVER_PAYMENT_METHODS, then return true")
    @Test
    fun returnTrueOnIsPaymentMethodsWidgetCallWithServerToServerType() {
        assertTrue(PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS.isPaymentMethodsWidget)
    }

    @DisplayName("Given isPaymentMethodsWidget is called, when widget type is CARD_PAYMENT, then return false")
    @Test
    fun returnFalseOnIsPaymentMethodsWidgetCallWithCardPaymentType() {
        assertFalse(PaymentWidgetType.CARD_PAYMENT.isPaymentMethodsWidget)
    }

    @DisplayName("Given isCardPaymentWidget is called, when widget type is CARD_PAYMENT, then return true")
    @Test
    fun returnTrueOnIsCardPaymentWidgetCallWithCardPaymentType() {
        assertTrue(PaymentWidgetType.CARD_PAYMENT.isCardPaymentWidget)
    }

    @DisplayName("Given isCardPaymentWidget is called, when widget type is PRE_AUTH, then return true")
    @Test
    fun returnTrueOnIsCardPaymentWidgetCallWithPreAuthType() {
        assertTrue(PaymentWidgetType.PRE_AUTH.isCardPaymentWidget)
    }

    @DisplayName("Given isCardPaymentWidget is called, when widget type is GOOGLE_PAY, then return false")
    @Test
    fun returnFalseOnIsCardPaymentWidgetCallWithGooglePayType() {
        assertFalse(PaymentWidgetType.GOOGLE_PAY.isCardPaymentWidget)
    }

    @DisplayName("Given isGooglePayWidget is called, when widget type is GOOGLE_PAY, then return true")
    @Test
    fun returnTrueOnIsGooglePayWidgetCallWithGooglePayType() {
        assertTrue(PaymentWidgetType.GOOGLE_PAY.isGooglePayWidget)
    }

    @DisplayName("Given isGooglePayWidget is called, when widget type is PRE_AUTH_GOOGLE_PAY, then return true")
    @Test
    fun returnTrueOnIsGooglePayWidgetCallWithPreAuthGooglePayType() {
        assertTrue(PaymentWidgetType.PRE_AUTH_GOOGLE_PAY.isGooglePayWidget)
    }

    @DisplayName("Given isGooglePayWidget is called, when widget type is PAYMENT_METHODS, then return false")
    @Test
    fun returnFalseOnIsGooglePayWidgetCallWithPaymentMethodsType() {
        assertFalse(PaymentWidgetType.PAYMENT_METHODS.isGooglePayWidget)
    }

    @DisplayName("Given isExposed is called, when widget type is PAY_BY_BANK, then return true")
    @Test
    fun returnTrueOnIsExposedCallWithPayByBankType() {
        assertTrue(PaymentWidgetType.PAY_BY_BANK_APP.isExposed)
    }

    @DisplayName("Given isExposed is called, when widget type is PAYMENT_METHODS, then return false")
    @Test
    fun returnFalseOnIsExposedCallWithPaymentMethodsType() {
        assertFalse(PaymentWidgetType.PAYMENT_METHODS.isExposed)
    }
}
