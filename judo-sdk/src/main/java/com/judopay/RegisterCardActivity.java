package com.judopay;

import android.os.Bundle;

import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_ID;

/**
 * Displays a form to the user, allowing for card to be registered and used for token transactions.
 * <br>
 * Mandatory extras:
 * <ol>
 * <li>{@link JudoPay#JUDO_ID} Judo ID of your account</li>
 * <li>{@link JudoPay#JUDO_CONSUMER} identifier for the consumer of the transaction</li>
 * </ol>
 * <br>
 */
public final class RegisterCardActivity extends JudoActivity {

    private RegisterCardFragment registerCardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRequiredExtras(JUDO_CONSUMER, JUDO_ID);

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