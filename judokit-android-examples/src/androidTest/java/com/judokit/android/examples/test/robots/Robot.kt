package com.judokit.android.examples.test.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.judokit.android.examples.R
import com.judokit.android.examples.test.espresso.setChecked
import com.judokit.android.examples.test.espresso.waitUntilVisible
import java.util.Properties

private const val CREDENTIALS_FILE_NAME = "test-credentials.properties"
private const val JUDO_ID = "judo-id"
private const val TOKEN = "token"
private const val SECRET = "secret"

class Robot {

    fun isOnScreen(screen: String) {
        when (screen) {
            "Main" -> onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        }
    }

    fun press(button: String) {
        when (button) {
            "Settings" -> onView(withId(R.id.action_settings)).perform(click())
            else -> onView(withText(button)).perform(click())
        }
    }

    fun tapOnText(text: String) {
        onView(withText(text)).perform(click())
    }

    fun enterTextIntoField(textToEnter: String, fieldName: String) {
        val view = when (fieldName) {
            "Card number" -> onView(withId(R.id.numberTextInputEditText))
            "Cardholder Name" -> onView(withId(R.id.nameTextInputEditText))
            "Expiry Date" -> onView(withId(R.id.expirationDateTextInputEditText))
            "Secure Code" -> onView(withId(R.id.securityNumberTextInputEditText))
            else -> onView(withId(R.id.numberTextInputEditText))
        }
        view.perform(replaceText(textToEnter))
    }

    fun isVisible(screen: String) {
        val view = when (screen) {
            "Main" -> onView(withId(R.id.recyclerView))
            "Results" -> onView(withText("JudoResult")).waitUntilVisible()
            else -> onView(withId(R.id.recyclerView))
        }
        view.check(matches(isDisplayed()))
    }

    fun configureJudo() {
        press("Settings")

        val props = Properties().apply {
            load(getInstrumentation().context.resources.assets.open(CREDENTIALS_FILE_NAME))
        }

        with(props) {
            val judoId = getProperty(JUDO_ID)
            val token = getProperty(TOKEN)
            val secret = getProperty(SECRET)

            onView(withText(R.string.judo_id_title)).perform(click())
            onView(withId(android.R.id.edit)).perform(replaceText(judoId))
            onView(withText("OK")).perform(click())

            onView(withText(R.string.token_title)).perform(click())
            onView(withId(android.R.id.edit)).perform(replaceText(token))
            onView(withText("OK")).perform(click())

            onView(withText(R.string.secret_title)).perform(click())
            onView(withId(android.R.id.edit)).perform(replaceText(secret))
            onView(withText("OK")).perform(click())

            pressBack()
        }
    }
}
