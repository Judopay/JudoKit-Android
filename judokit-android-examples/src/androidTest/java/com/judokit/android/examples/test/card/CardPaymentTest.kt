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
import com.judokit.android.examples.test.BuildConfig
import com.judopay.judokit.android.R
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardPaymentTest {

    object ValidCardDetails {
        const val CARD_NUMBER = "4976 3500 0000 6891"
        const val CARDHOLDER_NAME = "Test User"
        const val CARD_EXPIRY = "12/25"
        const val CARD_SECURITY_CODE = "341"
    }

    @get:Rule
    var permissionNotifications: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

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
            }
            .commit()
    }

    @Test
    fun onValidCardDetailsInputSubmitButtonShouldBeEnabled() {
        onView(withText("Pay with card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
    }

    @Test
    fun onCancelledTransactionErrorPopupShouldBeDisplayed() {
        onView(withText("Pay with card"))
            .perform(click())

        onView(withText("Cancel"))
            .perform(click())

        Thread.sleep(500)

        onView(withText("User cancelled the payment.")).check(matches(isDisplayed()))
    }

    @Test
    fun onSuccessfulTransactionReceiptObjectShouldContainRelevantInfo() {
        onView(withText("Pay with card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onDeclinedTransactionReceiptObjectShouldContainRelevantInfo() {
        onView(withText("Pay with card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            "12/30",
            "123"
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("3D secure authorisation declined", "", "Declined", "Payment")
    }

    @Test
    fun onFailedTransactionReceiptObjectShouldContainRelevantInfo() {
        onView(withText("Pay with card"))
            .perform(click())

        enterPaymentSheetDetails(
            "4111 1111 1111 1111",
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
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
        onView(withText("Pay with card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
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
    @Ignore("Skip until we can figure out how to assert PAD details are sent")
    fun primaryAccountDetailsSentWhenToggledOn() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("is_primary_account_details_enabled", true)
            }
            .commit()

        onView(withText("Pay with card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("Sorry, but your card authentication has failed."))))
    }

    @Test
    fun onSuccessfulPreauthTransactionReceiptObjectContainsRelevantInfo() {
        onView(withText("Pre-auth with card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun onSuccessfulRegisterCardTransactionReceiptObjectContainsRelevantInfo() {
        onView(withText("Register card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Register")
    }

    @Test
    fun onSuccessfulCheckCardTransactionReceiptObjectContainsRelevantInfo() {
        onView(withText("Check card"))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
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

        onView(withText("Token payments"))
            .perform(scrollTo())
            .perform(click())

        composeTestRule.onNodeWithText("TOKENIZE A NEW CARD")
            .assertIsEnabled()
            .performClick()

        Thread.sleep(500)

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            ValidCardDetails.CARDHOLDER_NAME,
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(TokenPaymentsActivity::class.java.name) {
            composeTestRule.onNodeWithText("Security code")
                .assertIsEnabled()
                .performTextInput(ValidCardDetails.CARD_SECURITY_CODE)

            closeSoftKeyboard()

            composeTestRule.onNodeWithText("PAYMENT")
                .assertIsEnabled()
                .performClick()
        }

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onSuccessfulFrictionlessPaymentReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }
            .commit()

        onView(withText("Pay with card"))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            "Frictionless Successful",
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
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

        onView(withText("Pay with card"))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            ValidCardDetails.CARD_NUMBER,
            "Frictionless NoMethod",
            ValidCardDetails.CARD_EXPIRY,
            ValidCardDetails.CARD_SECURITY_CODE
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun onSuccessfulPaymentMethodsCardPaymentReceiptObjectContainsRelevantInfo() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("should_payment_methods_verify_security_code", true)
            }
            .commit()

        onView(withText("Payment methods"))
            .perform(click())

        Thread.sleep(1000)

        try {
            onView(withId(R.id.addButton))
                .check(matches(isEnabled()))
                .perform(click())

            enterPaymentSheetDetails(
                ValidCardDetails.CARD_NUMBER,
                ValidCardDetails.CARDHOLDER_NAME,
                ValidCardDetails.CARD_EXPIRY,
                ValidCardDetails.CARD_SECURITY_CODE
            )

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText("341"))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "Payment")

        } catch (e: NoMatchingViewException) {
            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText("341"))

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

        onView(withText("Pre-auth payment methods"))
            .perform(click())

        Thread.sleep(1000)

        try {
            onView(withId(R.id.addButton))
                .check(matches(isEnabled()))
                .perform(click())

            enterPaymentSheetDetails(
                ValidCardDetails.CARD_NUMBER,
                ValidCardDetails.CARDHOLDER_NAME,
                ValidCardDetails.CARD_EXPIRY,
                ValidCardDetails.CARD_SECURITY_CODE
            )

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText("341"))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "PreAuth")

        } catch (e: NoMatchingViewException) {
            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText("341"))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "PreAuth")
        }
    }
}
