package com.judokit.android.examples.test.card

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher

class EspressoExtensions {
    companion object {
        /**
         * Perform action of waiting for a certain view within a single root view
         * @param matcher Generic Matcher used to find our view
         */
        fun searchFor(matcher: Matcher<View>): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isRoot()
                }

                override fun getDescription(): String {
                    return "Searching for view $matcher in the root view"
                }

                override fun perform(
                    uiController: UiController,
                    view: View,
                ) {
                    var tries = 3
                    val childViews: Iterable<View> = TreeIterables.breadthFirstViewTraversal(view)

                    childViews.forEach {
                        tries++
                        if (matcher.matches(it)) {
                            return
                        }
                    }

                    throw NoMatchingViewException.Builder()
                        .withRootView(view)
                        .withViewMatcher(matcher)
                        .build()
                }
            }
        }
    }
}
