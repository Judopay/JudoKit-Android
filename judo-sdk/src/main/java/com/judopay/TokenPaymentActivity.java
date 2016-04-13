package com.judopay;

import android.content.Intent;
import android.os.Bundle;

import static com.judopay.Judo.JUDO_OPTIONS;

/**
 * Displays a card entry form to the user, allowing for a token payment to be made.
 * To launch the TokenPaymentActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, TokenPaymentActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
 * .setJudoId("1234567")
 * .setCurrency(Currency.GBP)
 * .setAmount("1.99")
 * .setConsumerRef("consumerRef")
 * .setCardToken(cardToken)
 * .build());
 *
 * startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
 * </pre>
 * See {@link com.judopay.JudoOptions} for the full list of supported options.
 */
public final class TokenPaymentActivity extends JudoActivity {

    private TokenPaymentFragment tokenPaymentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (tokenPaymentFragment != null && !tokenPaymentFragment.isPaymentInProgress()){
            super.onBackPressed();
        }
    }

}