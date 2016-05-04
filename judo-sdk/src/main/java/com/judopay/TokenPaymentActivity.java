package com.judopay;

import android.content.Intent;
import android.os.Bundle;

/**
 * Displays a card entry form to the user, allowing for a token payment to be made.
 * To launch the TokenPaymentActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 * <p/>
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, TokenPaymentActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
 * .setJudoId("1234567")
 * .setCurrency(Currency.GBP)
 * .setAmount("1.99")
 * .setConsumerRef("consumerRef")
 * .setCardToken(cardToken)
 * .build());
 * <p/>
 * startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
 * </pre>
 * See {@link com.judopay.JudoOptions} for the full list of supported options.
 */
public final class TokenPaymentActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            fragment = new TokenPaymentFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

}