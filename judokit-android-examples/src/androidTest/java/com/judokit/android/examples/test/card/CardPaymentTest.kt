package com.judokit.android.examples.test.card

import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.judopay.judokit.android.R
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.result.ResultActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// https://github.com/android/testing-samples
// https://developer.android.com/training/testing/espresso

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardPaymentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(DemoFeatureListActivity::class.java)

    @Before
    fun setupSettings() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs
            .edit()
            .clear()
            .apply {
                putString("judo_id", "judoId")
                putBoolean("is_payment_session_enabled", false)
                putString("token", "token")
                putString("secret", "secret")
            }
    }

    @Test
    fun onValidCardDetailsInputSubmitButtonShouldBeEnabled() {
        onView(withText("Pay with card"))
            .perform(click())

        onView(withId(R.id.numberTextInputEditText))
            .perform(clearText(), typeText("4111 1111 1111 1111"))

        onView(withId(R.id.nameTextInputEditText))
            .perform(clearText(), typeText("John Doe"))

        onView(withId(R.id.expirationDateTextInputEditText))
            .perform(clearText(), typeText("11/25"))

        onView(withId(R.id.securityNumberTextInputEditText))
            .perform(clearText(), typeText("234"))

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(ResultActivity::class.java) {
            onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
                .check(matches(hasDescendant(withText("code"))))
        }
    }
}