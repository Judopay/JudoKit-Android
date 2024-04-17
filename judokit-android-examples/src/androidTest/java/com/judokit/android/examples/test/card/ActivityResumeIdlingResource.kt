package com.judokit.android.examples.test.card

import android.app.Activity
import androidx.test.espresso.IdlingResource
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

class ActivityResumeIdlingResource(
    val customName: String = "activity-resume-idling-resource",
    val activityClassName: String,
) : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private val isIdle = false

    private val currentActivity: Activity?
        get() {
            val registry = ActivityLifecycleMonitorRegistry.getInstance()
            return registry.getActivitiesInStage(Stage.RESUMED).firstOrNull()
        }

    override fun getName() = customName

    override fun isIdleNow(): Boolean {
        if (isIdle) return true
        val activity = currentActivity ?: return false
        val isIdle = activity.javaClass.name == activityClassName

        if (isIdle) {
            resourceCallback!!.onTransitionToIdle()
        }

        return isIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = resourceCallback
    }
}
