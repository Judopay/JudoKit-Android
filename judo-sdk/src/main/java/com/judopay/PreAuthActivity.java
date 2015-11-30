package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import com.judopay.model.Consumer;
import com.judopay.model.Receipt;

import static com.judopay.JudoPay.JUDO_AMOUNT;
import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_CURRENCY;
import static com.judopay.JudoPay.JUDO_ID;
import static com.judopay.JudoPay.JUDO_META_DATA;
import static com.judopay.JudoPay.JUDO_PAYMENT_REF;

/**
 * Displays a payment form to the user, allowing for a pre-auth to be made.
 * <br>
 * The {@link Receipt} containing the result of the payment transaction is
 * returned in the Activity result and can be either {@link JudoPay#RESULT_PAYMENT_SUCCESS},
 * {@link JudoPay#RESULT_PAYMENT_DECLINED} or {@link JudoPay#RESULT_ERROR} if an error occurred.
 * <br>
 * Mandatory extras:
 * <ol>
 * <li>{@link JudoPay#JUDO_ID} Judo ID of your account</li>
 * <li>{@link JudoPay#JUDO_AMOUNT} the total amount for the transaction</li>
 * <li>{@link JudoPay#JUDO_CURRENCY} the currency for the transaction (GBP, USD, CAD)</li>
 * <li>{@link JudoPay#JUDO_CONSUMER} identifier for the consumer of the transaction</li>
 * <li>{@link JudoPay#JUDO_PAYMENT_REF} identifier for the payment</li>
 * </ol>
 * <br>
 * Optional extras:
 * {@link JudoPay#JUDO_META_DATA} an optional key-value map of data to be included when making the
 * pre-auth transaction.
 */
public class PreAuthActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForExtras(JUDO_AMOUNT, JUDO_ID, JUDO_CURRENCY, JUDO_PAYMENT_REF, JUDO_CONSUMER);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            String judoId = intent.getStringExtra(JUDO_ID);
            String amount = intent.getStringExtra(JUDO_AMOUNT);
            String currency = intent.getStringExtra(JUDO_CURRENCY);
            String paymentRef = intent.getStringExtra(JUDO_PAYMENT_REF);
            Consumer consumer = intent.getParcelableExtra(JUDO_CONSUMER);

            //optional meta data
            Bundle metaData = intent.getBundleExtra(JUDO_META_DATA);

            PreAuthFragment fragment = PreAuthFragment.newInstance(judoId, amount, currency, paymentRef, consumer, metaData);

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