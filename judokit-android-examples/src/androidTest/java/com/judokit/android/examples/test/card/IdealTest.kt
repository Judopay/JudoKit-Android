package com.judokit.android.examples.test.card

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.judokit.android.examples.BuildConfig
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.card.FeaturesList.PAYMENT_METHODS_LABEL
import com.judopay.judokit.android.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class IdealTest {
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
                putString("judo_id", BuildConfig.IDEAL_JUDO_ID)
                putString("token", BuildConfig.IDEAL_API_TEST_TOKEN)
                putString("secret", BuildConfig.IDEAL_API_TEST_SECRET)
                putString("currency", "EUR")
                putStringSet("payment_methods", setOf("IDEAL"))
            }
            .commit()
    }

    @Test
    fun testSuccessfulIdealTransaction() {
        onView(withText(PAYMENT_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.payButton))
            .perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.idealWebView)).perform(swipeUp())
        clickButtonOnWebViewWithText(Ideal.NEXT_BUTTON)

        onView(withId(R.id.idealWebView)).perform(swipeUp())
        clickButtonOnWebViewWithText(Ideal.LOGIN_BUTTON)

        onView(withId(R.id.idealWebView)).perform(swipeUp())
        clickButtonOnWebViewWithText(Ideal.MAKE_PAYMENT_BUTTON)

        onView(withId(R.id.idealWebView)).perform(swipeUp())
        clickButtonOnWebViewWithText(Ideal.BACK_BUTTON)

        assertIdealReceiptObject("", "SUCCEEDED")
    }

    @Test
    fun testCancelIdealTransaction() {
        onView(withText(PAYMENT_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.payButton))
            .perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.idealWebView)).perform(swipeUp())
        clickButtonOnWebViewWithText(Ideal.ABORT_BUTTON)

        Thread.sleep(1500)

        onView(withText("The request has failed or responded without data.")).check(matches(isDisplayed()))
    }
}
