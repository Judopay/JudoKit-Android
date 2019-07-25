package com.judopay;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.judopay.error.RootedDeviceNotPermittedError;

/**
 * Base Activity class from which all other Activities should extend from.
 * This class provides two main functions:
 * <ol>
 * <li>Detect if the device is rooted, and throws a {@link RootedDeviceNotPermittedError},
 * preventing further access since we cannot guarantee the payment transaction will be secure.</li>
 * <li>Shows the back button in the action bar, allowing the user to navigate back easily.</li>
 * </ol>
 */
abstract class JudoActivity extends BaseActivity {
    protected static final String TAG_JUDO_FRAGMENT = "JudoFragment";

    protected JudoFragment fragment;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fragment = (JudoFragment) getSupportFragmentManager().findFragmentByTag(TAG_JUDO_FRAGMENT);
    }

    public void setProgressListener(final ProgressListener progressListener) {
        fragment.setProgressListener(progressListener);
    }

    public boolean isTransactionInProgress() {
        return fragment.isTransactionInProgress();
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && !fragment.isTransactionInProgress()) {
            setResult(Judo.RESULT_CANCELED);
            super.onBackPressed();
        }
    }
}
