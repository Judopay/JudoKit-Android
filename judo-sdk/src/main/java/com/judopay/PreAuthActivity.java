package com.judopay;

import android.content.Intent;
import android.os.Bundle;

/**
 * Displays a card entry form to the user, allowing for a pre-auth to be made.
 * To launch the PreAuthActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, PreAuthActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("1234567")
 * .setCurrency(Currency.GBP)
 * .setAmount("1.99")
 * .setConsumerReference("consumerRef")
 * .build());
 * startActivityForResult(intent, PRE_AUTH_REQUEST);
 * </pre>
 *
 * See {@link Judo} for the full list of supported options
 */
public final class PreAuthActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.payment);

        if (savedInstanceState == null) {
            fragment = new PreAuthFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, TAG_JUDO_FRAGMENT)
                    .commit();
        }
    }

}