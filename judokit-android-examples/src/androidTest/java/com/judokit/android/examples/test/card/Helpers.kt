package com.judokit.android.examples.test.card

import android.content.res.Configuration
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.platform.app.InstrumentationRegistry
import com.judokit.android.examples.test.card.EspressoExtensions.Companion.searchFor
import org.hamcrest.Matcher
import java.lang.Thread.sleep

fun doOnView(
    matcher: Matcher<View>,
    vararg actions: ViewAction,
) {
    actions.forEach {
        waitForView(matcher).perform(it)
    }
}

fun assertOnView(
    matcher: Matcher<View>,
    vararg assertions: ViewAssertion,
) {
    assertions.forEach {
        waitForView(matcher).check(it)
    }
}

/**
 * Perform action of implicitly waiting for a certain view.
 * This differs from EspressoExtensions.searchFor in that,
 * upon failure to locate an element, it will fetch a new root view
 * in which to traverse searching for our @param match
 *
 * @param viewMatcher ViewMatcher used to find our view
 */
fun waitForView(
    viewMatcher: Matcher<View>,
    waitMillis: Int = 10000,
    waitMillisPerTry: Long = 500,
): ViewInteraction {
    // Derive the max tries
    val maxTries = waitMillis / waitMillisPerTry.toInt()

    var tries = 0

    for (i in 0..maxTries) {
        try {
            // Track the amount of times we've tried
            tries++

            // Search the root for the view
            onView(isRoot()).perform(searchFor(viewMatcher))

            // If we're here, we found our view. Now return it
            return onView(viewMatcher)
        } catch (e: Exception) {
            if (tries == maxTries) {
                throw e
            }
            sleep(waitMillisPerTry)
        }
    }

    throw Exception("Error finding a view matching $viewMatcher")
}

fun isTablet(): Boolean {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val configuration = context.resources.configuration
    return configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}
