package com.judokit.android.examples.test.card

import android.Manifest
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import com.judokit.android.examples.test.BuildConfig
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Build
import com.judopay.judokit.android.R

@RunWith(AndroidJUnit4::class)
@LargeTest
class RavelinIntegrationTest {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
        }
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
                putBoolean("is_payment_session_enabled", true)
                putString("token", BuildConfig.API_TEST_TOKEN)
                putString("secret", BuildConfig.API_TEST_SECRET)
                putString("amount", "0.15")
                putString("currency", "GBP")
                putBoolean("should_ask`_for_csc", false)
                putBoolean("is_recommendation_feature_enabled", true)
                putString("rsa_key", BuildConfig.RSA_KEY)
            }
            .commit()
    }

    @Test
    fun testPaymentAllowAuthoriseLowValueNoPreference() {
        setupRavelin(ALLOW, AUTHORISE, LOW_VALUE, NO_PREFERENCE)

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

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun testPaymentReviewAuthenticateTRAChallenge() {
        setupRavelin(REVIEW, AUTHENTICATE, TRA, CHALLENGE_REQUESTED)

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
    fun testPaymentPreventAuthoriseLowValueChallengeAsMandate() {
        setupRavelin(PREVENT, AUTHORISE, LOW_VALUE, CHALLENGE_MANDATE)

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

        Thread.sleep(1000)

        onView(withText(TRANSACTION_PREVENTED)).check(matches(isDisplayed()))
    }

    @Test
    fun testPaymentAllowAuthenticateTRANoChallenge() {
        setupRavelin(ALLOW, AUTHENTICATE, TRA, NO_CHALLENGE)

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

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun testPaymentPreventAuthenticateTRANoPreference() {
        setupRavelin(PREVENT, AUTHENTICATE, TRA, NO_PREFERENCE)

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

        Thread.sleep(1000)

        onView(withText(TRANSACTION_PREVENTED)).check(matches(isDisplayed()))
    }

    @Test
    fun testPreAuthReviewAuthoriseLowValueChallengeAsMandate() {
        setupRavelin(REVIEW, AUTHORISE, LOW_VALUE, CHALLENGE_MANDATE)

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
    fun testPreAuthAllowAuthoriseTRANoChallenge() {
        setupRavelin(ALLOW, AUTHORISE, TRA, NO_CHALLENGE)

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

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun testPreAuthPreventAuthenticateLowValueNoChallenge() {
        setupRavelin(PREVENT, AUTHENTICATE, LOW_VALUE, NO_CHALLENGE)

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

        Thread.sleep(1000)

        onView(withText(TRANSACTION_PREVENTED)).check(matches(isDisplayed()))
    }

    @Test
    fun testPreAuthReviewAuthoriseTRANoPreference() {
        setupRavelin(REVIEW, AUTHORISE, TRA, NO_PREFERENCE)

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

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun testPreAuthAllowAuthenticateLowValueChallenge() {
        setupRavelin(ALLOW, AUTHENTICATE, LOW_VALUE, CHALLENGE_REQUESTED)

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
    fun testCheckCardAllowAuthoriseTRAChallengeAsMandate() {
        setupRavelin(ALLOW, AUTHORISE, TRA, CHALLENGE_MANDATE)

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
    fun testCheckCardReviewAuthenticateTRANoChallenge() {
        setupRavelin(REVIEW, AUTHENTICATE, TRA, NO_CHALLENGE)

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

        // Backend overrides CRI to Challenge as Mandate
        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "CheckCard")
    }

    @Test
    fun testCheckCardPreventAuthenticateLowValueChallenge() {
        setupRavelin(PREVENT, AUTHENTICATE, LOW_VALUE, CHALLENGE_REQUESTED)

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

        Thread.sleep(1000)

        onView(withText(TRANSACTION_PREVENTED)).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckCardAllowAuthenticateLowValueNoPreference() {
        setupRavelin(REVIEW, AUTHENTICATE, LOW_VALUE, NO_PREFERENCE)

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

        // Backend overrides CRI to Challenge as Mandate
        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "CheckCard")
    }

    @Test
    fun testCheckCardReviewAuthoriseLowValueChallenge() {
        setupRavelin(REVIEW, AUTHORISE, LOW_VALUE, CHALLENGE_REQUESTED)

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

        // Backend overrides CRI to Challenge as Mandate
        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "CheckCard")
    }
}
