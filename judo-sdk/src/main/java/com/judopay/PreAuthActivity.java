package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;
import static com.judopay.JudoPay.JUDO_META_DATA;
import static com.judopay.JudoPay.JUDO_PAYMENT_REF;

public class PreAuthActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_PAYMENT_REF, JUDO_CONSUMER);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            String judoId = intent.getStringExtra(JUDO_ID);
            String amount = intent.getStringExtra(JUDO_AMOUNT);
            String currency = intent.getStringExtra(JUDO_CURRENCY);
            String paymentRef = intent.getStringExtra(JUDO_PAYMENT_REF);
            Consumer consumer = intent.getParcelableExtra(JUDO_CONSUMER);

            //optional meta data
            Bundle metaData = intent.getBundleExtra(JUDO_META_DATA);

            PreAuthFragment fragment = PreAuthFragment.newInstance(judoId, amount, currency, paymentRef, consumer, metaData);

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

}