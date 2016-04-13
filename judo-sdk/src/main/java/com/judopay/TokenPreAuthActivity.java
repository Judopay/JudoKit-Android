package com.judopay;

import android.content.Intent;
import android.os.Bundle;

/**
 * Displays a card entry form to the user, allowing for a token payment to be made.
 * To launch the TokenPreAuthActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, TokenPreAuthActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
 * .setJudoId("1234567")
 * .setCurrency(Currency.GBP)
 * .setAmount("1.99")
 * .setConsumerRef("consumerRef")
 * .setCardToken(cardToken)
 * .build());
 *
 * startActivityForResult(intent, TOKEN_PRE_AUTH_REQUEST);
 * </pre>
 *
 * See {@link com.judopay.JudoOptions} for the full list of supported options.
 */
public class TokenPreAuthActivity extends JudoActivity {

    private TokenPreAuthFragment tokenPreAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (tokenPreAuthFragment != null && !tokenPreAuthFragment.isPaymentInProgress()) {
            super.onBackPressed();
        }
    }

}