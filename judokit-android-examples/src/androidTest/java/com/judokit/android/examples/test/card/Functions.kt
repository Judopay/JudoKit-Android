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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.platform.app.InstrumentationRegistry
import com.chuckerteam.chucker.api.Chucker
import com.judokit.android.examples.result.ResultActivity
import com.judokit.android.examples.test.BuildConfig
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.Matchers
import com.chuckerteam.chucker.R as ChuckerR
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
    Thread.sleep(30000)
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
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(9))
            .check(matches(ViewMatchers.hasDescendant(withText(startsWith(message)))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(14))
            .check(matches(ViewMatchers.hasDescendant(withText(CoreMatchers.not(receiptId)))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(15))
            .check(matches(ViewMatchers.hasDescendant(withText(result))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(16))
            .check(matches(ViewMatchers.hasDescendant(withText(type))))
    }
}

/*
 * Assert iDEAL transaction result object
 */
fun assertIdealReceiptObject(
    receiptId: String,
    result: String,
) {
    awaitActivityThenRun(ResultActivity::class.java.name) {
        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(13))
            .check(matches(ViewMatchers.hasDescendant(withText(CoreMatchers.not(receiptId)))))

        onView(withId(SDK.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(14))
            .check(matches(ViewMatchers.hasDescendant(withText(result))))
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

fun updateRecommendationUrlWith(suffix: String) {
    val recommendationURL = BuildConfig.RECOMMENDATION_URL
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

    Thread.sleep(20000)
}

fun fillBillingDetails(
    email: String,
    country: String,
    phone: String,
    addressLineOne: String,
    city: String,
    postcode: String,
    state: String? = null,
) {
    fillTextField(SDK.id.emailTextInputEditText, email)

    selectFromMultipleAndEnterText(SDK.id.countryTextInputEditText, country)

    onView(withText(country)).perform(click())

    Thread.sleep(500)

    if (country == "India" || country == "China") {
        if (state != null) {
            fillTextField(SDK.id.administrativeDivisionTextInputEditText, state)
            onView(withText(state)).perform(click())
        }
    }

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

fun assertUsingChucker(
    cri: String? = null,
    sca: String? = null,
    checkCRI: Boolean,
    checkSCA: Boolean,
    challenge: Boolean? = true,
) {
    openChucker()

    // Open transaction
    onView(withId(ChuckerR.id.transactionsRecyclerView))
        .check(matches(isDisplayed()))

    if (challenge == true) {
        onView(withId(ChuckerR.id.transactionsRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click(),
                ),
            )
    } else {
        onView(withId(ChuckerR.id.transactionsRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click(),
                ),
            )
    }

    // Open request
    onView(withId(ChuckerR.id.tabLayout))
        .check(matches(isDisplayed()))

    onView(
        allOf(
            withText("Request"),
        ),
    ).perform(click())

    // Assertion
    if (checkCRI && cri != null) {
        onView(withText(containsString("\"challengeRequestIndicator\": \"$cri\"")))
            .check(matches(isDisplayed()))
    }

    if (checkSCA && sca != null) {
        onView(withText(containsString("\"scaExemption\": \"$sca\"")))
            .check(matches(isDisplayed()))
    }
}

fun clickButtonOnWebViewWithText(text: String) {
    Web.onWebView()
        .withElement(DriverAtoms.findElement(Locator.XPATH, "//button[text()='$text']"))
        .perform(DriverAtoms.webClick())
}

fun openChucker() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    val intent = Chucker.getLaunchIntent(context)
    context.startActivity(intent)

    onView(withText("Chucker")).check(matches(isDisplayed()))

    Thread.sleep(5000)
}

fun assertPADTransaction() {
    openChucker()
    onView(withId(ChuckerR.id.transactionsRecyclerView))
        .check(matches(isDisplayed()))
    onView(withId(ChuckerR.id.transactionsRecyclerView))
        .perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click(),
            ),
        )
    onView(withId(ChuckerR.id.tabLayout))
        .check(matches(isDisplayed()))

    onView(
        allOf(
            withText("Request"),
        ),
    ).perform(click())
    onView(withText(containsString("\"primaryAccountDetails\": ")))
        .check(matches(isDisplayed()))
}
