package com.judopay;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Displays a card entry form to the user, allowing for card to be checked and used for token transactions.
 *
 * To launch the CheckCardActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent containing the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, CheckCardActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("1234567")
 * .setConsumerReference("consumerRef")
 * .build());
 *
 * startActivityForResult(intent, CHECK_CARD_REQUEST);
 * </pre>
 *
 * See {@link Judo} for the full list of supported options
 */
public final class CheckCardActivity extends JudoActivity {

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.check_card);

        if (savedInstanceState == null) {
            fragment = new CheckCardFragment();
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, TAG_JUDO_FRAGMENT)
                    .commit();
        }
    }
}
