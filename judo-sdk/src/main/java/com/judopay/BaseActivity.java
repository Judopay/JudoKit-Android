package com.judopay;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.judopay.error.RootedDeviceNotPermittedError;

import static com.judopay.Judo.JUDO_OPTIONS;
import static com.judopay.arch.TextUtil.isEmpty;
import static com.judopay.arch.ThemeUtil.getStringAttr;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Judo judo = getIntent().getParcelableExtra(JUDO_OPTIONS);

        if (RootDetector.isRooted() && !judo.isRootedDevicesAllowed()) {
            throw new RootedDeviceNotPermittedError();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Judo.JUDO_REQUEST) {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void setTitle(@StringRes final int titleId) {
        String activityTitle = getStringAttr(this, R.attr.activityTitle);
        if (isEmpty(activityTitle)) {
            super.setTitle(titleId);
        } else {
            super.setTitle(activityTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
