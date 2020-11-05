package com.judokit.android.examples.test.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.judokit.android.examples.R
import com.judokit.android.examples.test.espresso.setChecked
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentMethod
import org.hamcrest.CoreMatchers.anything
import java.util.Properties

private const val TAG_REQUIRE_3DS = "@require-3ds-config"
private const val TAG_REQUIRE_NON_3DS = "@require-non-3ds-config"
private const val TAG_REQUIRE_AVS = "@require-avs"
private const val TAG_REQUIRE_BUTTON_AMOUNT = "@require-button-amount"
private const val TAG_DISABLE_SECURITY_CODE = "@disable-security-code"
private const val TAG_ENABLE_CARD_PAYMENT_ONLY = "@enable-card-payment-only"
private const val TAG_ENABLE_PBBA_ONLY = "@enable-pbba-only"
private const val TAG_ENABLE_GOOGLE_PAY_ONLY = "@enable-google-pay-only"
private const val TAG_ENABLE_ALL_PAYMENT_METHODS = "@enable-all-payment-methods"
private const val TAG_DISABLE_ALL_PAYMENT_METHODS = "@disable-all-payment-methods"
private const val TAG_CURRENCY_GBP = "@currency-gbp"
private const val TAG_CURRENCY_EUR = "@currency-eur"

private const val CREDENTIALS_FILE_NAME = "test-credentials.properties"
private const val JUDO_ID = "judo-id"
private const val TOKEN = "token"
private const val THREE_DS_TOKEN = "3DS-token"
private const val SECRET = "secret"
private const val THREE_DS_SECRET = "3DS-secret"

class ConfigurationRobot {
    private val props = Properties().apply {
        load(
            InstrumentationRegistry.getInstrumentation().context.resources.assets.open(
                CREDENTIALS_FILE_NAME
            )
        )
    }

    fun configure(tags: MutableCollection<String>) {
        onView(withId(R.id.action_settings)).perform(click())

        setJudoId()
        for (tag in tags) {
            when (tag) {
                TAG_REQUIRE_NON_3DS -> {
                    setToken(threeDSecureEnabled = false)
                    setSecret(threeDSecureEnabled = false)
                }
                TAG_REQUIRE_3DS -> {
                    setToken(threeDSecureEnabled = true)
                    setSecret(threeDSecureEnabled = true)
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
                TAG_ENABLE_PBBA_ONLY ->
                    enablePaymentMethod(
                        mapOf(
                            Pair(PaymentMethod.CARD, false),
                            Pair(PaymentMethod.IDEAL, false),
                            Pair(PaymentMethod.GOOGLE_PAY, false),
                            Pair(PaymentMethod.PAY_BY_BANK, true)
                        )
                    )
                TAG_ENABLE_GOOGLE_PAY_ONLY ->
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

    private fun setJudoId() {
        val judoId = props.getProperty(JUDO_ID)
        onView(withText(R.string.judo_id_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setToken(threeDSecureEnabled: Boolean) {
        val judoId = props.getProperty(if (threeDSecureEnabled) THREE_DS_TOKEN else TOKEN)
        onView(withText(R.string.token_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setSecret(threeDSecureEnabled: Boolean) {
        val judoId = props.getProperty(if (threeDSecureEnabled) THREE_DS_SECRET else SECRET)
        onView(withText(R.string.secret_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
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
            7
        } else {
            8
        }
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.currency_title)),
                    click()
                )
            )
        onData(anything()).inAdapterView(withId(androidx.preference.R.id.select_dialog_listview))
            .atPosition(currencyPositionInList).perform(click())
    }
}

private val PaymentMethod.text
    get() = when (this) {
        PaymentMethod.CARD -> "Card"
        PaymentMethod.IDEAL -> "iDeal"
        PaymentMethod.GOOGLE_PAY -> "Google Pay"
        PaymentMethod.PAY_BY_BANK -> "Pay by Bank App"
    }
