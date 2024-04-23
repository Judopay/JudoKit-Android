package com.judokit.android.examples.test.card

import android.Manifest
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.feature.tokenpayments.TokenPaymentsActivity
import com.judokit.android.examples.result.ResultActivity
import com.judopay.judokit.android.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Build
import com.judokit.android.examples.test.card.BillingInfo.VALID_ADDRESS
import com.judokit.android.examples.test.card.BillingInfo.VALID_CITY
import com.judokit.android.examples.test.card.BillingInfo.VALID_COUNTRY
import com.judokit.android.examples.test.card.BillingInfo.VALID_EMAIL
import com.judokit.android.examples.test.card.BillingInfo.VALID_MOBILE
import com.judokit.android.examples.test.card.BillingInfo.VALID_PHONE_CODE
import com.judokit.android.examples.test.card.BillingInfo.VALID_POSTCODE
import com.judokit.android.examples.test.card.CardDetails.CARD_EXPIRY
import com.judokit.android.examples.test.card.CardDetails.CARD_NUMBER
import com.judokit.android.examples.test.card.CardDetails.CARD_SECURITY_CODE
import com.judokit.android.examples.test.card.CardPaymentTest.ValidCardDetails.CARDHOLDER_NAME
import com.judokit.android.examples.test.card.FeaturesList.CHECK_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PAYMENT_METHODS_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PAY_WITH_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PREAUTH_METHODS_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PREAUTH_WITH_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.REGISTER_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.TOKEN_PAYMENTS_LABEL
import com.judokit.android.examples.test.card.Other.CANCELLED_PAYMENT_TOAST
import com.judokit.android.examples.test.card.Other.CANCEL_BUTTON

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardPaymentTest {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    object ValidCardDetails {
        const val CARD_NUMBER = "4976 3500 0000 6891"
        const val CARDHOLDER_NAME = "Test User"
        const val CARD_EXPIRY = "12/25"
        const val CARD_SECURITY_CODE = "341"
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(DemoFeatureListActivity::class.java)

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    @Before
    fun setupSettings() {
        sharedPrefs
            .edit()
            .apply {
                putString("judo_id", BuildConfig.JUDO_ID)
                putString("challengeRequestIndicator", "CHALLENGE_AS_MANDATE")
                putBoolean("is_payment_session_enabled", false)
                putString("token", BuildConfig.API_TEST_TOKEN)
                putString("secret", BuildConfig.API_TEST_SECRET)
                putString("amount", "0.15")
                putString("currency", "GBP")
                putBoolean("should_ask_for_csc", false)
                putBoolean("is_recommendation_feature_enabled", false)
            }
            .commit()
    }

    @Test
    fun onValidCardDetailsInputSubmitButtonShouldBeEnabled() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
    }

    @Test
    fun onCancelledTransactionErrorPopupShouldBeDisplayed() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        onView(withText(CANCEL_BUTTON))
            .perform(click())

        Thread.sleep(500)

        onView(withText(CANCELLED_PAYMENT_TOAST)).check(matches(isDisplayed()))
    }

    @Test
    fun onSuccessfulTransactionReceiptObjectShouldContainRelevantInfo() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onDeclinedTransactionReceiptObjectShouldContainRelevantInfo() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            "12/30",
            "123"
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("", "", "Declined", "Payment")
    }

    @Test
    fun onFailedTransactionReceiptObjectShouldContainRelevantInfo() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            "4111 1111 1111 1111",
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            "123"
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("The gateway reported an error", "", "Error", "Payment")
    }

    @Test
    fun onCancel3DS2ChallengeScreenReceiptObjectShouldContainRelevantInfo() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun("com.judopay.judo3ds2.ui.challenge.ChallengeActivity") {
            onView(withText("CANCEL"))
                .perform(click())
        }

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
                .check(matches(hasDescendant(withText("Unable to process transaction. Card authentication failed with 3DS Server."))))
        }
    }

    @Test
    fun onSuccessfulPreauthTransactionReceiptObjectContainsRelevantInfo() {
        onView(withText(PREAUTH_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun onSuccessfulRegisterCardTransactionReceiptObjectContainsRelevantInfo() {
        onView(withText(REGISTER_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Register")
    }

    @Test
    fun onSuccessfulCheckCardTransactionReceiptObjectContainsRelevantInfo() {
        onView(withText(CHECK_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "CheckCard")
    }

    @Test
    fun onSuccessfulTokenPaymentReceiptObjectContainsRelevantInfo() {
        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        onView(withText(TOKEN_PAYMENTS_LABEL))
            .perform(scrollTo())
            .perform(click())

        composeTestRule.onNodeWithText("TOKENIZE A NEW CARD")
            .assertIsEnabled()
            .performClick()

        Thread.sleep(500)

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(TokenPaymentsActivity::class.java.name) {
            composeTestRule.onNodeWithText("Security code")
                .assertIsEnabled()
                .performTextInput(CARD_SECURITY_CODE)

            closeSoftKeyboard()

            composeTestRule.onNodeWithText("PAYMENT")
                .assertIsEnabled()
                .performClick()
        }

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onSuccessfulTokenPreauthReceiptObjectContainsRelevantInfo() {
        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        onView(withText(TOKEN_PAYMENTS_LABEL))
            .perform(scrollTo())
            .perform(click())

        composeTestRule.onNodeWithText("TOKENIZE A NEW CARD")
            .assertIsEnabled()
            .performClick()

        Thread.sleep(500)

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(TokenPaymentsActivity::class.java.name) {
            composeTestRule.onNodeWithText("Security code")
                .assertIsEnabled()
                .performTextInput(CARD_SECURITY_CODE)

            closeSoftKeyboard()

            composeTestRule.onNodeWithText("PRE-AUTH")
                .assertIsEnabled()
                .performClick()
        }

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun onSuccessfulFrictionlessPaymentReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }
            .commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless Successful",
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onFrictionlessNoMethodPaymentReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }
            .commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless NoMethod",
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onFrictionlessAuthFailedPaymentReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }
            .commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless AuthFailed",
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
                .check(matches(hasDescendant(withText("Unable to process transaction. Card authentication failed with 3DS Server."))))
        }
    }

    @Test
    fun onSuccessfulPaymentMethodsCardPaymentReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("should_payment_methods_verify_security_code", true)
            }
            .commit()

        onView(withText(PAYMENT_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        try {
            onView(withId(R.id.addButton))
                .check(matches(isEnabled()))
                .perform(click())

            enterPaymentSheetDetails(
                CARD_NUMBER,
                CARDHOLDER_NAME,
                CARD_EXPIRY,
                CARD_SECURITY_CODE
            )

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "Payment")

        } catch (e: NoMatchingViewException) {
            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "Payment")
        }
    }

    @Test
    fun onSuccessfulPaymentMethodsPreauthReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("should_payment_methods_verify_security_code", true)
            }
            .commit()

        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        onView(withText(PREAUTH_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        try {
            onView(withId(R.id.addButton))
                .check(matches(isEnabled()))
                .perform(click())

            enterPaymentSheetDetails(
                CARD_NUMBER,
                CARDHOLDER_NAME,
                CARD_EXPIRY,
                CARD_SECURITY_CODE
            )

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "PreAuth")

        } catch (e: NoMatchingViewException) {
            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "PreAuth")
        }
    }

    @Test
    fun onUserCanSuccessfullyRemoveCardInPaymentMethods() {
        onView(withText(PAYMENT_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.addButton))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            "4222 0000 0122 7408",
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.subTitle))
            .check(matches(withText("Visa Ending 7408")))

        onView(withId(R.id.editButton))
            .check(matches(isEnabled()))
            .perform(click())

        onView(withId(R.id.removeCardIcon))
            .check(matches(isEnabled()))
            .perform(click())

        onView(withText("Delete"))
            .perform(click())

        Thread.sleep(1000)

        onView(withText("Visa Ending 7408"))
            .check(doesNotExist())
    }

    @Test
    fun testSuccessfulPaymentWithBillingDetails() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("should_ask_for_billing_information", true)
            }
            .commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(500)

        fillBillingDetails(
            VALID_EMAIL,
            VALID_COUNTRY,
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            VALID_POSTCODE
        )

        onView(withId(R.id.billingDetailsSubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }
}
