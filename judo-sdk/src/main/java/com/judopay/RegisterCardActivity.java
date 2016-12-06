package com.judopay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Displays a card entry form to the user, allowing for card to be registered and used for token transactions.
 *
 * To launch the RegisterCardActivity, call {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent the configuration options:
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, RegisterCardActivity.class);
 * intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("1234567")
 * .setConsumerReference("consumerRef")
 * .build());
 *
 * startActivityForResult(intent, REGISTER_CARD_REQUEST);
 * </pre>
 * See {@link Judo} for the full list of supported options
 */
public final class RegisterCardActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.add_card);

        if (savedInstanceState == null) {
            fragment = new RegisterCardFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public void onDeclined() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_card_failed)
                .setMessage(R.string.please_check_details_try_again)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create()
                .show();
    }

}