package com.judokit.android.examples.test.card

import androidx.annotation.IdRes
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResourceTimeoutException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.judokit.android.examples.result.ResultActivity
import com.judopay.judokit.android.R as SDK
import com.judokit.android.examples.R as Examples
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.startsWith
import com.judokit.android.examples.test.BuildConfig

private val context = InstrumentationRegistry.getInstrumentation().targetContext
private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

/*
* Wait for an activity to be resumed before running a function
*/
fun awaitActivityThenRun(activityClassName: String, func: () -> Unit) {
    val idlingResource = ActivityResumeIdlingResource(
        customName = "[activity-check-idling-resource]-${activityClassName}",
        activityClassName = activityClassName)

    awaitIdlingResourceThenRun(idlingResource, func)
}

/*
 * Wait for an idling resource to be idle before running a function
 */
fun <IR: IdlingResource>awaitIdlingResourceThenRun(idlingResource: IR, func: () -> Unit) = IdlingRegistry.getInstance().apply {
    register(idlingResource)
    func()
    unregister(idlingResource)
}

/*
 * Complete the 3DS2 process
 */
fun clickCompleteOn3DS2Screen() {
    awaitActivityThenRun("com.judopay.judo3ds2.ui.challenge.ChallengeActivity") {
        try {
            Thread.sleep(1000)
            Espresso.onView(ViewMatchers.withText("COMPLETE"))
                .perform(ViewActions.longClick())

            Thread.sleep(1000)
            Espresso.onView(ViewMatchers.withText("COMPLETE"))
                .perform(ViewActions.longClick())

            Thread.sleep(1000)
            Espresso.onView(ViewMatchers.withText("COMPLETE"))
                .perform(ViewActions.longClick())

        } catch (e: IdlingResourceTimeoutException) {
            println("Global Pay screen unable to continue")
        }

    }
}

/*
 * Assert receipt object values
 */
fun assertReceiptObject(message: String, receiptId: String, result: String, type: String) {
    awaitActivityThenRun(ResultActivity::class.java.name) {
        Espresso.onView(ViewMatchers.withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(8))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(startsWith(message)))))

        Espresso.onView(ViewMatchers.withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(13))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(CoreMatchers.not(receiptId)))))

        Espresso.onView(ViewMatchers.withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(14))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(result))))

        Espresso.onView(ViewMatchers.withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(15))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(type))))
    }
}

/*
 * Fill card payment sheet
 */
fun enterPaymentSheetDetails(cardNumber: String, cardHolder: String, cardExpiry: String, cardSecurityCode: String) {
    fillTextField(SDK.id.numberTextInputEditText, cardNumber)

    fillTextField(SDK.id.nameTextInputEditText, cardHolder)

    fillTextField(SDK.id.expirationDateTextInputEditText, cardExpiry)

    fillTextField(SDK.id.securityNumberTextInputEditText, cardSecurityCode)
}

private fun fillTextField(@IdRes textFieldId: Int, text: String) {
    Espresso.onView((ViewMatchers.withId(textFieldId)))
        .perform(ViewActions.clearText(), ViewActions.typeText(text))
}

fun setupRavelin(action: String, toa: String, exemption: String, challenge: String) {
    val recommendationURL = BuildConfig.RECOMMENDATION_URL
    val suffix = "$action/$toa/$exemption/$challenge"
    sharedPrefs
        .edit()
        .apply {
            putString("recommendation_url", recommendationURL + suffix)
        }
        .commit()

    Espresso.onView(ViewMatchers.withId(Examples.id.action_settings))
        .perform(ViewActions.click())

    Espresso.onView(ViewMatchers.withText("Generate payment session"))
        .perform(ViewActions.click())

    Thread.sleep(1000)
}
