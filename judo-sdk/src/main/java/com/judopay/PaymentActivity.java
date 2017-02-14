package com.judopay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Displays a card entry form to the user, allowing for a payment to be made.
 * To launch the PaymentActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, PaymentActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("1234567")
 * .setCurrency(Currency.GBP)
 * .setAmount("1.99")
 * .setConsumerReference("consumerRef")
 * .build());
 * startActivityForResult(intent, PAYMENT_REQUEST);
 * </pre>
 * See {@link Judo} for the full list of supported options
 */
public final class PaymentActivity extends JudoActivity {

    private static final String TAG_JUDO_FRAGMENT = "JudoFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.payment);

        fragment = (JudoFragment) getFragmentManager().findFragmentByTag(TAG_JUDO_FRAGMENT);

        Log.d("PaymentFragment", String.format("Fragment is null? %b", fragment == null));

        if (fragment == null) {
            fragment = new PaymentFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, TAG_JUDO_FRAGMENT)
                    .commit();
        }

        Log.d("PaymentFragment", "Instance: " + fragment.toString());
    }

}