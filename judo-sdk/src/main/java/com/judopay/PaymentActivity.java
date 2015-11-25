package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import static com.judopay.JudoPay.*;

public class PaymentActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_PAYMENT_REF, JUDO_CONSUMER);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            Consumer consumer = intent.getParcelableExtra(JUDO_CONSUMER);
            String judoId = intent.getStringExtra(JUDO_ID);
            String amount = intent.getStringExtra(JUDO_AMOUNT);
            String currency = intent.getStringExtra(JUDO_CURRENCY);
            String paymentRef = intent.getStringExtra(JUDO_PAYMENT_REF);

            //optional meta data
            Bundle metaData = intent.getBundleExtra(JUDO_META_DATA);

            PaymentFragment paymentFragment = PaymentFragment.newInstance(judoId, amount, currency, paymentRef, consumer, metaData);

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

}