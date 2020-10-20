package com.judokit.android.examples.test.espresso

import android.view.View
import android.widget.Checkable
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description
import java.util.concurrent.TimeoutException

const val TIMEOUT_INTERVAL = 500L
const val DEFAULT_TIMEOUT = 10000L

fun ViewInteraction.waitUntilVisible(timeout: Long = DEFAULT_TIMEOUT): ViewInteraction {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout
    do {
        try {
            check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            return this
        } catch (e: NoMatchingViewException) {
            Thread.sleep(TIMEOUT_INTERVAL)
        }
    } while (System.currentTimeMillis() < endTime)
    throw TimeoutException()
}

fun setChecked(checked: Boolean) = object : ViewAction {
    val checkableViewMatcher = object : BaseMatcher<View>() {
        override fun matches(item: Any?): Boolean = isA(Checkable::class.java).matches(item)
        override fun describeTo(description: Description?) {
            description?.appendText("is Checkable instance ")
        }
    }

    override fun getConstraints(): BaseMatcher<View> = checkableViewMatcher
    override fun getDescription(): String? = null
    override fun perform(uiController: UiController?, view: View) {
        val checkableView: Checkable = view as Checkable
        checkableView.isChecked = checked
    }
}
