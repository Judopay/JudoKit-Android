package com.judokit.android.examples.test.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.judokit.android.examples.R
import com.judokit.android.examples.test.espresso.waitUntilVisible
import com.judokit.android.examples.test.exceptions.ViewNotDefinedException
import com.judokit.android.examples.test.robots.enum.View
import com.judokit.android.examples.test.robots.enum.ViewType
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not

class CardEntryRobot {

    fun isOnScreen(screen: String) {
        when (screen) {
            View.MAIN.value -> onView(withId(R.id.sampleAppConstraintLayout)).check(
                matches(
                    isDisplayed()
                )
            )
            else -> throw throw ViewNotDefinedException(
                CardEntryRobot::class.qualifiedName,
                screen
            )
        }
    }

    fun press(button: String) {
        onView(withText(button)).perform(click())
    }

    fun tapOn(text: String, type: String) {
        when (type) {
            ViewType.TEXT_FIELD.value ->
                when (text) {
                    View.SECURE_CODE.value -> onView(withId(R.id.securityNumberTextInputEditText)).perform(
                        click()
                    )
                    View.CARDHOLDER_NAME.value -> onView(withId(R.id.nameTextInputEditText)).perform(
                        click()
                    )
                    else -> throw throw ViewNotDefinedException(
                        CardEntryRobot::class.qualifiedName,
                        text
                    )
                }
            else -> onView(withText(text)).perform(click())
        }
    }

    fun enterTextIntoField(textToEnter: String, fieldName: String) {
        val view = when (fieldName) {
            View.CARD_NUMBER.value -> onView(withId(R.id.numberTextInputEditText))
            View.CARDHOLDER_NAME.value -> onView(withId(R.id.nameTextInputEditText))
            View.EXPIRY_DATE.value -> onView(withId(R.id.expirationDateTextInputEditText))
            View.SECURE_CODE.value -> onView(withId(R.id.securityNumberTextInputEditText))
            else -> onView(withId(R.id.numberTextInputEditText))
        }
        view.perform(click(), replaceText(textToEnter))
    }

    fun isVisible(view: String) {
        val matchedView = when (view) {
            View.MAIN.value -> onView(withId(R.id.sampleAppConstraintLayout))
            View.RESULTS.value -> onView(withText("JudoResult")).waitUntilVisible()
            View.INVALID_CARD_NUMBER.value -> onView(
                allOf(
                    withId(R.id.errorTextView),
                    isDescendantOfA(withId(R.id.numberTextInputLayout))
                )
            )
            View.CHECK_EXPIRY_DATE.value -> onView(
                allOf(
                    withId(R.id.errorTextView),
                    isDescendantOfA(withId(R.id.expirationDateTextInputLayout))
                )
            )
            View.CHECK_CVV.value -> onView(
                allOf(
                    withId(R.id.errorTextView),
                    isDescendantOfA(withId(R.id.securityNumberTextInputLayout))
                )
            )
            else -> throw ViewNotDefinedException(CardEntryRobot::class.qualifiedName, view)
        }
        matchedView.check(matches(isDisplayed()))
    }

    fun isDisabled(button: String) {
        onView(withText(button)).check(matches(not(isEnabled())))
    }

    fun isValueOfFieldEqual(field: String, value: String) {
        onView(ViewMatchers.withTagValue(CoreMatchers.`is`(field))).check(
            matches(withText(containsString(value)))
        )
    }
}
