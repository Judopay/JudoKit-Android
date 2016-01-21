package com.judopay;

import android.os.Bundle;

import static com.judopay.Judo.JUDO_CONSUMER;
import static com.judopay.Judo.JUDO_ID;
import static com.judopay.Judo.JUDO_OPTIONS;

/**
 * Displays a form to the user, allowing for card to be registered and used for token transactions.
 * <br>
 * Mandatory extras:
 * <ol>
 * <li>{@link Judo#JUDO_ID} Judo ID of your account</li>
 * <li>{@link Judo#JUDO_CONSUMER} identifier for the consumer of the transaction</li>
 * </ol>
 * <br>
 */
public final class RegisterCardActivity extends JudoActivity {

    private RegisterCardFragment registerCardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra((JUDO_OPTIONS))) {
            JudoOptions options = getIntent().getParcelableExtra(JUDO_OPTIONS);

            if (options.getConsumerRef() == null || options.getJudoId() == null) {
                throw new IllegalArgumentException("Intent must contain all required extras for RegisterCardActivity");
            }
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