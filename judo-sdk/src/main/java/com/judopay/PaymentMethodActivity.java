package com.judopay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class PaymentMethodActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.judo_payment_method);

        if (savedInstanceState == null) {
            Fragment paymentMethodFragment = PaymentMethodFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, paymentMethodFragment, null)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Judo.RESULT_CANCELED) {
            setResult(resultCode, data);
            finish();
        }
    }
}