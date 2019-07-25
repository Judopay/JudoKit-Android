package com.judopay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Displays a card entry form to the user, allowing for card to be registered and used for token transactions.
 *
 * To launch the RegisterCardActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent containing the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, RegisterCardActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("1234567")
 * .setConsumerReference("consumerRef")
 * .build());
 *
 * startActivityForResult(intent, REGISTER_CARD_REQUEST);
 * </pre>
 *
 * See {@link Judo} for the full list of supported options
 */
public final class RegisterCardActivity extends JudoActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.add_card);

        if (savedInstanceState == null) {
            fragment = new RegisterCardFragment();
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, TAG_JUDO_FRAGMENT)
                    .commit();
        }
    }
}
