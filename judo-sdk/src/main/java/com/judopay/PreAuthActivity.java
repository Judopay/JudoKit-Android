package com.judopay;

import android.os.Bundle;

import com.judopay.model.Receipt;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;

/**
 * Displays a payment form to the user, allowing for a pre-auth to be made.
 * <br>
 * The {@link Receipt} containing the result of the payment transaction is
 * returned in the Activity result and can be either {@link JudoPay#RESULT_SUCCESS},
 * {@link JudoPay#RESULT_DECLINED} or {@link JudoPay#RESULT_ERROR} if an error occurred.
 * <br>
 * Mandatory extras:
 * <ol>
 * <li>{@link JudoPay#JUDO_ID} Judo ID of your account</li>
 * <li>{@link JudoPay#JUDO_AMOUNT} the total amount for the transaction</li>
 * <li>{@link JudoPay#JUDO_CURRENCY} the currency for the transaction (GBP, USD, CAD)</li>
 * <li>{@link JudoPay#JUDO_CONSUMER} identifier for the consumer of the transaction</li>
 * </ol>
 * <br>
 * Optional extras:
 * {@link JudoPay#JUDO_META_DATA} an optional key-value map of data to be included when making the
 * pre-auth transaction.
 */
public final class PreAuthActivity extends JudoActivity {

    private PreAuthFragment preAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRequiredExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_CONSUMER);

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