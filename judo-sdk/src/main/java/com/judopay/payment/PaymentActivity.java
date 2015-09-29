package com.judopay.payment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity implements PaymentListener {

    public static final String JUDO_PAYMENT = "Judo-Payment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validateParcelableExtra(JUDO_PAYMENT);

        Parcelable payment = getIntent().getParcelableExtra(JUDO_PAYMENT);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, CardPaymentFragment.newInstance(payment, this))
                    .commit();
        }
    }

    private void validateParcelableExtra(String extraName) {
        Parcelable extra = getIntent().getParcelableExtra(extraName);
        if (extra == null) {
            throw new IllegalArgumentException(String.format("%s extra must be supplied to %s", extraName,
                    this.getClass().getSimpleName()));
        }
    }

}