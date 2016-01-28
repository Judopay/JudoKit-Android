package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import static com.judopay.Judo.JUDO_AMOUNT;
import static com.judopay.Judo.JUDO_CONSUMER;
import static com.judopay.Judo.JUDO_CURRENCY;
import static com.judopay.Judo.JUDO_ID;
import static com.judopay.Judo.JUDO_OPTIONS;

/**
 * Displays a card entry form to the user, allowing for a pre-auth to be made.
 *
 * To launch the PreAuthActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, PreAuthActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
 *      .setJudoId("1234567")
 *      .setCurrency(Currency.GBP)
 *      .setAmount("1.99")
 *      .setConsumerRef("consumerRef")
 *      .build());
 *
 * startActivityForResult(intent, PRE_AUTH_REQUEST);
 * </pre>
 *
 * See {@link JudoOptions} for the full list of supported options
 */
public final class PreAuthActivity extends JudoActivity {

    private PreAuthFragment preAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra((JUDO_OPTIONS))) {
            JudoOptions options = getIntent().getParcelableExtra(JUDO_OPTIONS);
            checkRequiredExtras(options.getAmount(), options.getJudoId(), options.getCurrency(), options.getConsumerRef());
        } else {
            checkRequiredExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_CONSUMER);
        }

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            preAuthFragment = new PreAuthFragment();
            preAuthFragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, preAuthFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!preAuthFragment.isPaymentInProgress()) {
            super.onBackPressed();
        }
    }

}