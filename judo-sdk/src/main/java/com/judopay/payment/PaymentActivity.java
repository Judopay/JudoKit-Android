package com.judopay.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.judopay.JudoPay;

import retrofit.Response;

public class PaymentActivity extends AppCompatActivity implements PaymentListener {

    public static final String JUDO_PAYMENT = "Judo-Payment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validateParcelableExtra(JUDO_PAYMENT);

        if (savedInstanceState == null) {
            Parcelable payment = getIntent().getParcelableExtra(JUDO_PAYMENT);
            Fragment fragment = CardPaymentFragment.newInstance(payment, this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
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

    @Override
    public void onPaymentSuccess(Response<PaymentResponse> response) {
        setResult(JudoPay.SUCCESS, new Intent());
        finish();
    }

    @Override
    public void onFailure(Throwable t) {
        setResult(JudoPay.ERROR, new Intent());
    }

}