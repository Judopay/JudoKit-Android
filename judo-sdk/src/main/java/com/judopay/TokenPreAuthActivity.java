package com.judopay;

import android.os.Bundle;

import static com.judopay.Judo.JUDO_AMOUNT;
import static com.judopay.Judo.JUDO_CARD_TOKEN;
import static com.judopay.Judo.JUDO_CONSUMER;
import static com.judopay.Judo.JUDO_CURRENCY;
import static com.judopay.Judo.JUDO_ID;
import static com.judopay.Judo.JUDO_OPTIONS;

public class TokenPreAuthActivity extends JudoActivity {

    private TokenPreAuthFragment tokenPreAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra((JUDO_OPTIONS))) {
            JudoOptions options = getIntent().getParcelableExtra(JUDO_OPTIONS);

            if (options.getAmount() == null || options.getJudoId() == null || options.getCurrency() == null || options.getConsumerRef() == null || options.getCardToken() == null) {
                throw new IllegalArgumentException("Intent must contain all required extras for TokenPreAuthActivity");
            }
        } else {
            checkRequiredExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_CONSUMER, JUDO_CARD_TOKEN);
        }

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