package com.judokit.android.examples.test.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.judokit.android.examples.R
import com.judokit.android.examples.test.espresso.setChecked
import com.judokit.android.examples.test.model.TestConfiguration
import com.judokit.android.examples.test.model.TestData
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.PaymentMethod

private const val TAG_REQUIRE_3DS = "@require-3ds-config"
private const val TAG_REQUIRE_NON_3DS = "@require-non-3ds-config"
private const val TAG_REQUIRE_AVS = "@require-avs"
private const val TAG_REQUIRE_BUTTON_AMOUNT = "@require-button-amount"
private const val TAG_DISABLE_SECURITY_CODE = "@disable-security-code"
private const val TAG_ENABLE_CARD_PAYMENT_ONLY = "@require-card-payment-enabled-only"
private const val TAG_ENABLE_IDEAL = "@require-ideal-payment-method"
private const val TAG_ENABLE_PBBA = "@require-pbba-payment-method"
private const val TAG_ENABLE_GOOGLE_PAY = "@require-apple-pay-google-pay-payment-method"
private const val TAG_ENABLE_ALL_PAYMENT_METHODS = "@require-all-payment-methods-enabled"
private const val TAG_DISABLE_ALL_PAYMENT_METHODS = "@require-all-payment-methods-disabled"
private const val TAG_CURRENCY_GBP = "@require-currency-gbp"
private const val TAG_CURRENCY_EUR = "@require-currency-eur"

class ConfigurationRobot {

    fun configure(tags: MutableCollection<String>, testData: TestConfiguration) {
        onView(withId(R.id.action_settings)).perform(click())

        setJudoId(testData.judoId)
        if (!testData.sandbox) {
            setSandboxDisabled()
        }
        for (tag in tags) {
            when (tag) {
                TAG_REQUIRE_NON_3DS -> {
                    setToken(testData.token)
                    setSecret(testData.secret)
                }
                TAG_REQUIRE_3DS -> {
                    setToken(testData.threeDSToken)
                    setSecret(testData.threeDSSecret)
                }
                TAG_REQUIRE_AVS -> setAvsEnabled()
                TAG_REQUIRE_BUTTON_AMOUNT -> setDisplayAmountOnButton()
                TAG_DISABLE_SECURITY_CODE -> disableSecurityCode()
                TAG_CURRENCY_EUR -> setCurrency(Currency.EUR)
                TAG_CURRENCY_GBP -> setCurrency(Currency.GBP)
                TAG_ENABLE_CARD_PAYMENT_ONLY ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, true),
                            Pair(PaymentMethod.IDEAL, false),
                            Pair(PaymentMethod.GOOGLE_PAY, false),
                            Pair(PaymentMethod.PAY_BY_BANK, false)
                        )
                    )
                TAG_ENABLE_PBBA ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, false),
                            Pair(PaymentMethod.IDEAL, false),
                            Pair(PaymentMethod.GOOGLE_PAY, false),
                            Pair(PaymentMethod.PAY_BY_BANK, true)
                        )
                    )
                TAG_ENABLE_IDEAL ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, false),
                            Pair(PaymentMethod.IDEAL, true),
                            Pair(PaymentMethod.GOOGLE_PAY, false),
                            Pair(PaymentMethod.PAY_BY_BANK, false)
                        )
                    )
                TAG_ENABLE_GOOGLE_PAY ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, false),
                            Pair(PaymentMethod.IDEAL, false),
                            Pair(PaymentMethod.GOOGLE_PAY, true),
                            Pair(PaymentMethod.PAY_BY_BANK, false)
                        )
                    )
                TAG_DISABLE_ALL_PAYMENT_METHODS ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, false),
                            Pair(PaymentMethod.IDEAL, false),
                            Pair(PaymentMethod.GOOGLE_PAY, false),
                            Pair(PaymentMethod.PAY_BY_BANK, false)
                        )
                    )
                TAG_ENABLE_ALL_PAYMENT_METHODS ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, true),
                            Pair(PaymentMethod.IDEAL, true),
                            Pair(PaymentMethod.GOOGLE_PAY, true),
                            Pair(PaymentMethod.PAY_BY_BANK, true)
                        )
                    )
            }
        }
        pressBack()
    }

    private fun setJudoId(judoId: String) {
        onView(withText(R.string.judo_id_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setToken(token: String) {
        onView(withText(R.string.token_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(token))
        onView(withText("OK")).perform(click())
    }

    private fun setSecret(secret: String) {
        onView(withText(R.string.secret_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(secret))
        onView(withText("OK")).perform(click())
    }

    private fun setSandboxDisabled() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.is_sandboxed_title)),
                    click()
                )
            )
    }

    private fun setAvsEnabled() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.avs_title)),
                    click()
                )
            )
    }

    private fun setDisplayAmountOnButton() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.should_payment_button_display_amount_title)),
                    click()
                )
            )
    }

    private fun disableSecurityCode() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.security_code_title)),
                    click()
                )
            )
    }

    private fun enablePaymentMethod(paymentMethods: Map<PaymentMethod, Boolean>) {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.payment_methods_title)),
                    click()
                )
            )
        paymentMethods.forEach {
            onView(withText(it.key.text)).perform(setChecked(it.value))
        }
        onView(withText("OK")).perform(click())
    }

    private fun setCurrency(currency: Currency) {
        val currencyPositionInList = if (currency == Currency.EUR) {
            2
        } else {
            0
        }
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.currency_title)),
                    click()
                )
            )
//        onData(anything()).inAdapterView(withId(androidx.preference.R.id.select_dialog_listview))
//            .atPosition(currencyPositionInList).perform(click())
    }

    companion object {
        var scenarioData: TestData? = null
        lateinit var testConfiguration: TestConfiguration
    }
}

private val PaymentMethod.text
    get() = when (this) {
        PaymentMethod.CARD -> "Card"
        PaymentMethod.IDEAL -> "iDeal"
        PaymentMethod.GOOGLE_PAY -> "Google Pay"
        PaymentMethod.PAY_BY_BANK -> "Pay by Bank App"
    }
