package com.judopay;

import android.support.test.espresso.IdlingResource;

public class JudoTransactionIdlingResource implements IdlingResource {

    private IdlingResource.ResourceCallback resourceCallback;
    private JudoActivity activity;
    private ProgressListener progressListener;

    public JudoTransactionIdlingResource(JudoActivity activity) {
        this.activity = activity;

        progressListener = new ProgressListener() {
            @Override
            public void onProgressShown() {

            }

            @Override
            public void onProgressDismissed() {
                if (resourceCallback == null) {
                    return;
                }
                //Called when the resource goes from busy to idle.
                resourceCallback.onTransitionToIdle();
            }
        };

        if (activity != null) {
            activity.setProgressListener(progressListener);
        }
    }

    @Override
    public String getName() {
        return "My idling resource";
    }

    @Override
    public boolean isIdleNow() {
        // the resource becomes idle when the progress has been dismissed
        return !activity.isTransactionInProgress();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}