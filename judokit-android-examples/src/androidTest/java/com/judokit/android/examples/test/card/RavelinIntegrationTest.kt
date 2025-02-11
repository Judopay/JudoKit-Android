package com.judokit.android.examples.test.card

import android.Manifest
import android.os.Build
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
import com.judokit.android.examples.result.ResultActivity
import com.judokit.android.examples.test.BuildConfig
import com.judokit.android.examples.test.card.CardDetails.CARDHOLDER_NAME
import com.judokit.android.examples.test.card.CardDetails.CARD_EXPIRY
import com.judokit.android.examples.test.card.CardDetails.CARD_NUMBER
import com.judokit.android.examples.test.card.CardDetails.CARD_SECURITY_CODE
import com.judokit.android.examples.test.card.FeaturesList.PAY_WITH_CARD_LABEL
import com.judokit.android.examples.test.card.Other.TRANSACTION_HALTED
import com.judokit.android.examples.test.card.Other.TRANSACTION_PREVENTED
import com.judokit.android.examples.test.card.Ravelin.CHALLENGE_MANDATE
import com.judokit.android.examples.test.card.Ravelin.CHALLENGE_REQUESTED
import com.judokit.android.examples.test.card.Ravelin.LOW_VALUE
import com.judokit.android.examples.test.card.Ravelin.NO_CHALLENGE
import com.judokit.android.examples.test.card.Ravelin.NO_PREFERENCE
import com.judokit.android.examples.test.card.Ravelin.TRA
import com.judopay.judokit.android.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
                putStringSet("payment_methods", setOf("CARD"))
            }
            .commit()
    }

    @Test
    fun testPreventTransaction() {
        updateRecommendationUrlWith("7")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withText(TRANSACTION_PREVENTED)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testReviewWithChallengeTransaction() {
        updateRecommendationUrlWith("25")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertUsingChucker(CHALLENGE_REQUESTED, LOW_VALUE, checkCRI = true, checkSCA = true)
    }

    @Test
    fun testAllowNoPreferenceTransaction() {
        updateRecommendationUrlWith("19")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertUsingChucker(NO_PREFERENCE, TRA, checkCRI = true, checkSCA = true, challenge = false)
    }

    @Test
    fun testReviewNoChallengeTransaction() {
        updateRecommendationUrlWith("24")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertUsingChucker(NO_CHALLENGE, LOW_VALUE, checkCRI = true, checkSCA = true, challenge = false)
    }

    @Test
    fun testAllowChallengeAsMandateTransaction() {
        updateRecommendationUrlWith("18")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertUsingChucker(CHALLENGE_MANDATE, LOW_VALUE, checkCRI = true, checkSCA = true)
    }

    @Test
    fun testWithoutSendingCRITransaction() {
        updateRecommendationUrlWith("35")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertUsingChucker(LOW_VALUE, checkCRI = false, checkSCA = true, challenge = false)
    }

    @Test
    fun testWithoutSendingSCATransaction() {
        updateRecommendationUrlWith("60")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertUsingChucker(NO_CHALLENGE, checkCRI = true, checkSCA = false, challenge = false)
    }

    @Test
    fun testWithoutSendingSCAAndCRITransaction() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "DON_T_SET")
                putString("scaExemption", "DON_T_SET")
            }
            .commit()

        updateRecommendationUrlWith("71")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertUsingChucker(checkCRI = false, checkSCA = false, challenge = true)
    }

    @Test
    fun testUsingSDKConfigTransaction() {
        updateRecommendationUrlWith("78")

        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "CHALLENGE_AS_MANDATE")
                putString("scaExemption", "TRANSACTION_RISK_ANALYSIS")
                putBoolean("halt_transaction_in_case_of_any_error_enabled", false)
            }
            .commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertUsingChucker(CHALLENGE_MANDATE, TRA, checkCRI = true, checkSCA = true, challenge = true)
    }

    @Test
    fun testPreventWithEmptyObjectTransaction() {
        updateRecommendationUrlWith("67")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(2500)

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withText(TRANSACTION_PREVENTED)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testHaltTransactionUponErrorSwitch() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("halt_transaction_in_case_of_any_error_enabled", true)
            }
            .commit()

        updateRecommendationUrlWith("5")

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(5000)

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withText(TRANSACTION_HALTED)).check(matches(isDisplayed()))
        }
    }
}
