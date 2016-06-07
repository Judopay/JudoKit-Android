package com.judopay.samples;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.samples.settings.SettingsActivity;
import com.judopay.samples.settings.SettingsPrefs;

import static com.judopay.Judo.JUDO_RECEIPT;
import static com.judopay.Judo.PAYMENT_REQUEST;
import static com.judopay.Judo.PRE_AUTH_REQUEST;
import static com.judopay.Judo.REGISTER_CARD_REQUEST;
import static com.judopay.Judo.SANDBOX;
import static com.judopay.Judo.TOKEN_PAYMENT_REQUEST;
import static com.judopay.Judo.TOKEN_PRE_AUTH_REQUEST;

/**
 * Sample app screen containing buttons to activate the different features of the Judo SDK
 * <br>
 * Update the {@link #JUDO_ID} string with the Judo ID from the judo website: http://www.judopay.com,
 */
@SuppressWarnings({"UnusedParameters", "WrongConstant"})
public class MainActivity extends BaseActivity {

    private static final String AMOUNT = "0.99";

    private static final String JUDO_ID = "100407-196";
    private static final String API_TOKEN = "823Eja2fEM6E9NAE";
    private static final String API_SECRET = "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6";

    private static final String CONSUMER_REF = "AndroidSdkSampleConsumerRef";

    private Judo judo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        judo = new Judo.Builder()
                .setJudoId(JUDO_ID)
                .setApiToken(API_TOKEN)
                .setApiSecret(API_SECRET)
                .setEnvironment(SANDBOX)
                .setAmount(AMOUNT)
                .setCurrency(getCurrency())
                .setConsumerRef(CONSUMER_REF)
                .build();

        setConfiguration();
    }

    public void performPayment(View view) {
        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(Judo.JUDO_OPTIONS, judo);
        startActivityForResult(intent, PAYMENT_REQUEST);
    }

    public void performPreAuth(View view) {
        Intent intent = new Intent(this, PreAuthActivity.class);

        intent.putExtra(Judo.JUDO_OPTIONS, judo);
        startActivityForResult(intent, PRE_AUTH_REQUEST);
    }

    public void performRegisterCard(View view) {
        Intent intent = new Intent(this, RegisterCardActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, judo);

        startActivityForResult(intent, REGISTER_CARD_REQUEST);
    }

    public void performTokenPreAuth(View view) {
        Receipt receipt = getLastReceipt();
        if (receipt != null) {
            Intent intent = new Intent(this, PreAuthActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, judo.newBuilder()
                    .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .build());

            startActivityForResult(intent, TOKEN_PRE_AUTH_REQUEST);
        } else {
            Toast.makeText(this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    public void performTokenPayment(View view) {
        Receipt receipt = getLastReceipt();

        if (receipt != null) {
            Intent intent = new Intent(this, PaymentActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, judo.newBuilder()
                    .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .build());

            startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
        } else {
            Toast.makeText(this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrency() {
        return getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(CURRENCY_KEY, Currency.GBP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setConfiguration();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu_item:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PAYMENT_REQUEST:
            case PRE_AUTH_REQUEST:
            case TOKEN_PAYMENT_REQUEST:
            case TOKEN_PRE_AUTH_REQUEST:
                handleResult(resultCode, data);
                break;

            case REGISTER_CARD_REQUEST:
                handleRegisterCardResult(resultCode, data);
                break;
        }
    }

    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void handleRegisterCardResult(int resultCode, Intent data) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);
                saveReceipt(receipt);
                showTokenPaymentDialog(receipt);
                break;
        }
    }

    private void showTokenPaymentDialog(final Receipt receipt) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.make_token_payment))
                .setMessage(getString(R.string.registered_card_can_perform_token_payments))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startTokenPayment(receipt);
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void startTokenPayment(Receipt receipt) {
        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(Judo.JUDO_OPTIONS, judo.newBuilder()
                .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                .setCardToken(receipt.getCardDetails())
                .build());

        startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
    }

    private void handleResult(int resultCode, Intent data) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.payment_successful))
                        .setMessage("Receipt ID: " + response.getReceiptId())
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;

            case Judo.RESULT_ERROR:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.transaction_error))
                        .setMessage(getString(R.string.could_not_perform_transaction_check_settings))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }

    private void setConfiguration() {
        SettingsPrefs settingsPrefs = new SettingsPrefs(this);
        this.judo = judo.newBuilder()
                .setAvsEnabled(settingsPrefs.isAvsEnabled())
                .setMaestroEnabled(settingsPrefs.isMaestroEnabled())
                .setAmexEnabled(settingsPrefs.isAmexEnabled())
                .build();
    }

}