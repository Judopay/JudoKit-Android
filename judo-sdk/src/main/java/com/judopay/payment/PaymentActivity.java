package com.judopay.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import com.judopay.JudoPay;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PaymentActivity extends AppCompatActivity implements PaymentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validateParcelableExtra(EXTRA_PAYMENT);

        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        this.setTitle(title != null ? title : "Payment");

        if (savedInstanceState == null) {
            Payment payment = getIntent().getParcelableExtra(EXTRA_PAYMENT);
            PaymentFragment fragment = PaymentFragment.newInstance(payment, this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(JudoPay.RESULT_CANCELED);
    }

    @Override
    public void onPaymentSuccess(PaymentResponse paymentResponse) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, paymentResponse);

        setResult(JudoPay.RESULT_PAYMENT_SUCCESS, intent);

        finish();
    }

    @Override
    public void onPaymentDeclined(PaymentResponse paymentResponse) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, paymentResponse);

        setResult(JudoPay.RESULT_PAYMENT_DECLINED, intent);

        finish();
    }

    private void validateParcelableExtra(String extraName) {
        Parcelable extra = getIntent().getParcelableExtra(extraName);
        if (extra == null) {
            throw new IllegalArgumentException(String.format("%s extra must be supplied to %s", extraName,
                    this.getClass().getSimpleName()));
        }
    }

}