package com.judopay.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.test.espresso.IdlingResource;

import static android.support.test.internal.util.Checks.checkNotNull;

public class ActivityFinishedIdlingResource implements IdlingResource, Application.ActivityLifecycleCallbacks {

    private boolean idleNow;
    private final String activityName;
    private ResourceCallback resourceCallback;

    public ActivityFinishedIdlingResource(Activity activity) {
        checkNotNull(activity);

        this.idleNow = activity.isFinishing();
        this.activityName = activity.getClass().getName();

        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    @Override
    public String getName() {
        return String.format("%s-%s", ActivityFinishedIdlingResource.class.getName(), activityName);
    }

    @Override
    public boolean isIdleNow() {
        if (idleNow) {
            if (resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
        }
        return idleNow;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        makeIdleAndUnregister(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        makeIdleAndUnregister(activity);
    }

    public void makeIdleAndUnregister(Activity activity) {
        this.idleNow = true;
        if (resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }

        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        makeIdleAndUnregister(activity);
    }
}
