package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import com.judopay.model.CardToken;
import com.judopay.model.Consumer;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CARD_TOKEN;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;
import static com.judopay.JudoPay.JUDO_META_DATA;
import static com.judopay.JudoPay.JUDO_PAYMENT_REF;

public class TokenPaymentActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            String judoId = intent.getStringExtra(JUDO_ID);
            String amount = intent.getStringExtra(JUDO_AMOUNT);
            String currency = intent.getStringExtra(JUDO_CURRENCY);
            String paymentRef = intent.getStringExtra(JUDO_PAYMENT_REF);
            Consumer consumer = intent.getParcelableExtra(JUDO_CONSUMER);
            CardToken cardToken = intent.getParcelableExtra(JUDO_CARD_TOKEN);

            //optional meta data
            Bundle metaData = intent.getBundleExtra(JUDO_META_DATA);

            TokenPaymentFragment fragment = TokenPaymentFragment.newInstance(judoId, amount, currency, cardToken, paymentRef, consumer, metaData);

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