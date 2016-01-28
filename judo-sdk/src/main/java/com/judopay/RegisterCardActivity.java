package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import static com.judopay.Judo.JUDO_CONSUMER;
import static com.judopay.Judo.JUDO_ID;
import static com.judopay.Judo.JUDO_OPTIONS;

/**
 * Displays a card entry form to the user, allowing for card to be registered and used for token transactions.
 *
 * To launch the RegisterCardActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, RegisterCardActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
 *      .setJudoId("1234567")
 *      .setConsumerRef("consumerRef")
 *      .build());
 *
 * startActivityForResult(intent, REGISTER_CARD_REQUEST);
 * </pre>
 *
 * See {@link JudoOptions} for the full list of supported options
 */
public final class RegisterCardActivity extends JudoActivity {

    private RegisterCardFragment registerCardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra((JUDO_OPTIONS))) {
            JudoOptions options = getIntent().getParcelableExtra(JUDO_OPTIONS);
            checkRequiredExtras(options.getConsumerRef(), options.getJudoId());
        } else {
            checkRequiredExtras(JUDO_CONSUMER, JUDO_ID);
        }

        setTitle(R.string.add_card);

        if (savedInstanceState == null) {
            registerCardFragment = new RegisterCardFragment();
            registerCardFragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, registerCardFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!registerCardFragment.isPaymentInProgress()) {
            super.onBackPressed();
        }
    }
}