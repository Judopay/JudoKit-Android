package com.judopay;

import android.os.Bundle;

import com.judopay.model.Receipt;

import static com.judopay.Judo.JUDO_AMOUNT;
import static com.judopay.Judo.JUDO_CONSUMER;
import static com.judopay.Judo.JUDO_CURRENCY;
import static com.judopay.Judo.JUDO_ID;

/**
 * Displays a payment form to the user, allowing for a payment to be made.
 * <br>
 * The {@link Receipt} containing the result of the payment transaction is
 * returned in the Activity result and can be either {@link Judo#RESULT_SUCCESS},
 * {@link Judo#RESULT_DECLINED} or {@link Judo#RESULT_ERROR} if an error occurred.
 * <br>
 * Mandatory extras:
 * <ol>
 * <li>{@link Judo#JUDO_ID} Judo ID of your account</li>
 * <li>{@link Judo#JUDO_AMOUNT} the total amount for the transaction</li>
 * <li>{@link Judo#JUDO_CURRENCY} the currency for the transaction (GBP, USD, CAD)</li>
 * <li>{@link Judo#JUDO_CONSUMER} identifier for the consumer of the transaction</li>
 * </ol>
 * <br>
 * Optional extras:
 * {@link Judo#JUDO_META_DATA} an optional key-value map of data to be included when making the
 * payment transaction.
 */
public final class PaymentActivity extends JudoActivity {

    private PaymentFragment paymentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRequiredExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_CONSUMER);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            paymentFragment = new PaymentFragment();
            paymentFragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, paymentFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!paymentFragment.isPaymentInProgress()) {
            super.onBackPressed();
        }
    }

}