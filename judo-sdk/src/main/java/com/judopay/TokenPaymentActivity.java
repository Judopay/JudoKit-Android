package com.judopay;

import android.os.Bundle;

import static com.judopay.Judo.JUDO_AMOUNT;
import static com.judopay.Judo.JUDO_CARD_TOKEN;
import static com.judopay.Judo.JUDO_CONSUMER;
import static com.judopay.Judo.JUDO_CURRENCY;
import static com.judopay.Judo.JUDO_ID;

public final class TokenPaymentActivity extends JudoActivity {

    private TokenPaymentFragment tokenPaymentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRequiredExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_CONSUMER, JUDO_CARD_TOKEN);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            tokenPaymentFragment = new TokenPaymentFragment();
            tokenPaymentFragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, tokenPaymentFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!tokenPaymentFragment.isPaymentInProgress()) {
            super.onBackPressed();
        }
    }

}