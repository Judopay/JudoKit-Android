package com.judokit.android.examples.test.card

import androidx.annotation.IdRes
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.judokit.android.examples.result.ResultActivity
import com.judokit.android.examples.test.BuildConfig
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.Matchers
import com.judokit.android.examples.R as Examples
import com.judopay.judokit.android.R as SDK

private val context = InstrumentationRegistry.getInstrumentation().targetContext
private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

/*
* Wait for an activity to be resumed before running a function
*/
fun awaitActivityThenRun(
    activityClassName: String,
    func: () -> Unit,
) {
    val idlingResource =
        ActivityResumeIdlingResource(
            customName = "[activity-check-idling-resource]-$activityClassName",
            activityClassName = activityClassName,
        )

    awaitIdlingResourceThenRun(idlingResource, func)
}

/*
 * Wait for an idling resource to be idle before running a function
 */
fun <IR : IdlingResource> awaitIdlingResourceThenRun(
    idlingResource: IR,
    func: () -> Unit,
) = IdlingRegistry.getInstance().apply {
    register(idlingResource)
    func()
    unregister(idlingResource)
}

/*
 * Complete the 3DS2 process
 */
fun clickCompleteOn3DS2Screen() {
    assertOnView(withText("COMPLETE"))
    Thread.sleep(15000)
    onView(withText("COMPLETE"))
        .perform(ViewActions.longClick())
}

/*
 * Assert receipt object values
 */
fun assertReceiptObject(
    message: String,
    receiptId: String,
    result: String,
    type: String,
) {
    awaitActivityThenRun(ResultActivity::class.java.name) {
        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(8))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText(startsWith(message)))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(13))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText(CoreMatchers.not(receiptId)))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(14))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText(result))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(15))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText(type))))
    }
}

/*
 * Fill card payment sheet
 */
fun enterPaymentSheetDetails(
    cardNumber: String,
    cardHolder: String,
    cardExpiry: String,
    cardSecurityCode: String,
) {
    fillTextField(SDK.id.numberTextInputEditText, cardNumber)

    fillTextField(SDK.id.nameTextInputEditText, cardHolder)

    fillTextField(SDK.id.expirationDateTextInputEditText, cardExpiry)

    fillTextField(SDK.id.securityNumberTextInputEditText, cardSecurityCode)
}

fun fillTextField(
    @IdRes textFieldId: Int,
    text: String,
) {
    doOnView(withId(textFieldId), clearText(), typeText(text))
}

fun setupRavelin(
    action: String,
    toa: String,
    exemption: String,
    challenge: String,
) {
    val recommendationURL = BuildConfig.RECOMMENDATION_URL
    val suffix = "$action/$toa/$exemption/$challenge"
    sharedPrefs
        .edit()
        .apply {
            putString("recommendation_url", recommendationURL + suffix)
        }
        .commit()

    onView(withId(Examples.id.action_settings))
        .perform(click())

    onView(withText("Generate payment session"))
        .perform(click())

    Thread.sleep(1000)
}

fun fillBillingDetails(
    email: String,
    country: String,
    phone: String,
    addressLineOne: String,
    city: String,
    postcode: String,
) {
    fillTextField(SDK.id.emailTextInputEditText, email)

    selectFromMultipleAndEnterText(SDK.id.countryTextInputEditText, country)

    onView(withText(country)).perform(click())

    Thread.sleep(500)

    fillTextField(SDK.id.mobileNumberTextInputEditText, phone)

    fillTextField(SDK.id.addressLine1TextInputEditText, addressLineOne)

    fillTextField(SDK.id.cityTextInputEditText, city)

    fillTextField(SDK.id.postalCodeTextInputEditText, postcode)
}

fun selectFromMultipleAndEnterText(
    @IdRes textFieldId: Int,
    text: String,
) {
    onView(
        Matchers.allOf(
            withId(textFieldId),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
        ),
    ).perform(clearText(), typeText(text))
}

fun toggleBillingInfoSetting(state: Boolean) {
    sharedPrefs
        .edit()
        .apply {
            putBoolean("should_ask_for_billing_information", state)
        }
        .commit()
}
