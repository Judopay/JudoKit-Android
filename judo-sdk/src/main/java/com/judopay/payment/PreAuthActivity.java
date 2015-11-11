package com.judopay.payment;

import android.content.Intent;
import android.os.Bundle;
import com.judopay.JudoActivity;
import com.judopay.JudoPay;
import com.judopay.R;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PreAuthActivity extends JudoActivity implements PaymentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            Payment payment = getIntent().getParcelableExtra(EXTRA_PAYMENT);
            PreAuthFragment fragment = PreAuthFragment.newInstance(payment, this);

            getFragmentManager()
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