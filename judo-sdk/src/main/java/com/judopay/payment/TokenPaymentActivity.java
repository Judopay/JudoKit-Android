package com.judopay.payment;

import android.content.Intent;
import android.os.Bundle;

import com.judopay.JudoActivity;
import com.judopay.JudoPay;

public class TokenPaymentActivity extends JudoActivity implements PaymentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            TokenPayment tokenPayment = getIntent().getParcelableExtra(JudoPay.EXTRA_TOKEN_PAYMENT);

            TokenPaymentFragment paymentFragment = new TokenPaymentFragment();
            paymentFragment.setPaymentListener(this);

            Bundle arguments = new Bundle();
            arguments.putParcelable(TokenPaymentFragment.KEY_TOKEN_PAYMENT, tokenPayment);
            paymentFragment.setArguments(arguments);

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, paymentFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(JudoPay.RESULT_CANCELED);
    }

    @Override
    public void onPaymentSuccess(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

        setResult(JudoPay.RESULT_PAYMENT_SUCCESS, intent);

        finish();
    }

    @Override
    public void onPaymentDeclined(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

        setResult(JudoPay.RESULT_PAYMENT_DECLINED, intent);

        finish();
    }

}