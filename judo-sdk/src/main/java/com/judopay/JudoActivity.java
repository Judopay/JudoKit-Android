package com.judopay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.judopay.exception.RootUserBlockedException;

import static com.judopay.Judo.JUDO_OPTIONS;
import static com.judopay.Judo.RESULT_CONNECTION_ERROR;
import static com.judopay.Judo.RESULT_DECLINED;
import static com.judopay.Judo.RESULT_ERROR;
import static com.judopay.Judo.RESULT_SUCCESS;

/**
 * Base Activity class from which all other Activities should extend from.
 * This class provides two main functions:
 * <ol>
 * <li>Detect if the device is rooted, and throws a {@link RootUserBlockedException},
 * preventing further access since we cannot guarantee the payment transaction will be secure.</li>
 * <li>Shows the back button in the action bar, allowing the user to navigate back easily.</li>
 * </ol>
 */
abstract class JudoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(JUDO_OPTIONS)) {
            throw new IllegalArgumentException(String.format("%s Intent Extra is required for %s", JUDO_OPTIONS, this.getClass().getSimpleName()));
        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Judo.JUDO_REQUEST) {
            switch (resultCode) {
                case RESULT_SUCCESS:
                case RESULT_ERROR:
                    setResult(resultCode, data);
                    finish();
                    break;

                case RESULT_CONNECTION_ERROR:
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.connection_error)
                            .setMessage(R.string.please_check_your_internet_connection)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;

                case RESULT_DECLINED:
                    onDeclined();
                    break;
            }
        }
    }

    protected void onDeclined() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_failed)
                .setMessage(R.string.please_check_details_try_again)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        if (getIntent().hasExtra(Judo.JUDO_OPTIONS)) {
            JudoOptions options = getIntent().getParcelableExtra(Judo.JUDO_OPTIONS);

            if (options.getActivityTitle() != null) {
                super.setTitle(options.getActivityTitle());
            } else {
                super.setTitle(titleId);
            }
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
        for (Object object : objects) {
            if (object == null) {
                throw new IllegalArgumentException("JudoOptions must contain all required fields for Activity");
            }
        }
    }
}