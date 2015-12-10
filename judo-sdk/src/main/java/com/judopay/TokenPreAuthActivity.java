package com.judopay;

import android.os.Bundle;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CARD_TOKEN;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;

public class TokenPreAuthActivity extends JudoActivity {

    private TokenPreAuthFragment tokenPreAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRequiredExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_CONSUMER, JUDO_CARD_TOKEN);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            tokenPreAuthFragment = new TokenPreAuthFragment();
            tokenPreAuthFragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, tokenPreAuthFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!tokenPreAuthFragment.isPaymentInProgress()) {
            super.onBackPressed();
        }
    }

}