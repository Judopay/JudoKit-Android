package com.judopay;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.judopay.security.RootDetector;
import com.judopay.security.RootUserBlockedException;

/**
 * Base Activity class from which all other Activities should extend from.
 * This class provides two main functions:
 * <ol>
 * <li>Detect if the device is rooted, and throws a {@link com.judopay.security.RootUserBlockedException},
 * preventing further access since we cannot guarantee the payment transaction will be secure.</li>
 * <li>Shows the back button in the action bar, allowing the user to navigate back easily.</li>
 * </ol>
 */
public abstract class JudoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (RootDetector.isRooted() && !Judo.isRootedDevicesAllowed()) {
            throw new RootUserBlockedException();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        if (getIntent().hasExtra(Intent.EXTRA_TITLE)) {
            super.setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
        } else {
            super.setTitle(titleId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Judo.RESULT_CANCELED);
    }

    void checkJudoOptionsExtras(Object... objects) {
        for(Object object : objects) {
            if(object == null) {
                throw new IllegalArgumentException("JudoOptions must contain all required fields for Activity");
            }
        }
    }

    void checkRequiredExtras(String... keys) {
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            throw new RuntimeException(String.format("Activity %s must be started with Intent Extras", this.getClass().getSimpleName()));
        }
        for (String key : keys) {
            if (!extras.containsKey(key)) {
                throw new IllegalArgumentException(String.format("Extra '%s' is required for %s", key, this.getClass().getSimpleName()));
            }
        }
    }

}