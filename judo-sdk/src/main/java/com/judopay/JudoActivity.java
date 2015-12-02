package com.judopay;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.judopay.model.CardToken;
import com.judopay.model.Consumer;
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

        if (RootDetector.isRooted() && !JudoPay.isRootedDevicesAllowed()) {
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

    protected void checkRequiredExtras(String... keys) {
        Bundle extras = getIntent().getExtras();
        for (String key : keys) {
            if (!extras.containsKey(key)) {
                throw new IllegalArgumentException(String.format("Extra '%s' is required for %s", key, this.getClass().getSimpleName()));
            }
        }
    }

    public static void startPaymentActivity(Activity activity, int requestCode, String judoId, String amount, String currency, Consumer consumer, String paymentRef, Bundle metaData) {
        Intent intent = new Intent(activity, PaymentActivity.class);

        intent.putExtra(JudoPay.JUDO_ID, judoId);
        intent.putExtra(JudoPay.JUDO_AMOUNT, amount);
        intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
        intent.putExtra(JudoPay.JUDO_CONSUMER, consumer);
        intent.putExtra(JudoPay.JUDO_PAYMENT_REF, paymentRef);
        intent.putExtra(JudoPay.JUDO_META_DATA, metaData);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void startPreAuthActivity(Activity activity, int requestCode, String judoId, String amount, String currency, Consumer consumer, String paymentRef, Bundle metaData) {
        Intent intent = new Intent(activity, PreAuthActivity.class);

        intent.putExtra(JudoPay.JUDO_ID, judoId);
        intent.putExtra(JudoPay.JUDO_AMOUNT, amount);
        intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
        intent.putExtra(JudoPay.JUDO_CONSUMER, consumer);
        intent.putExtra(JudoPay.JUDO_PAYMENT_REF, paymentRef);
        intent.putExtra(JudoPay.JUDO_META_DATA, metaData);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void startTokenPreAuthActivity(Activity activity, int requestCode, String judoId, String amount, String currency, Consumer consumer, String yourPaymentReference, CardToken cardToken, Bundle metaData) {
        Intent intent = new Intent(activity, TokenPreAuthActivity.class);

        intent.putExtra(JudoPay.JUDO_ID, judoId);
        intent.putExtra(JudoPay.JUDO_AMOUNT, amount);
        intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
        intent.putExtra(JudoPay.JUDO_CONSUMER, consumer);
        intent.putExtra(JudoPay.JUDO_PAYMENT_REF, yourPaymentReference);
        intent.putExtra(JudoPay.JUDO_CARD_TOKEN, cardToken);
        intent.putExtra(JudoPay.JUDO_META_DATA, metaData);

        activity.startActivityForResult(intent, requestCode);
    }
}