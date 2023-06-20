package com.judokit.android.examples.test.card

import android.app.Activity
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource

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