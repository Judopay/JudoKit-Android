package com.judopay.samples;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.TokenPaymentActivity;
import com.judopay.TokenPreAuthActivity;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.samples.settings.SettingsActivity;
import com.judopay.samples.settings.SettingsPrefs;

import static com.judopay.Judo.JUDO_RECEIPT;
import static com.judopay.Judo.PAYMENT_REQUEST;
import static com.judopay.Judo.PRE_AUTH_REQUEST;
import static com.judopay.Judo.REGISTER_CARD_REQUEST;
import static com.judopay.Judo.TOKEN_PAYMENT_REQUEST;
import static com.judopay.Judo.TOKEN_PRE_AUTH_REQUEST;

/**
 * Sample app screen containing buttons to activate the different features of the Judo SDK
 * <br>
 * Update the {@link #JUDO_ID} string with the Judo ID from the judo website: http://www.judopay.com,
 * Update the {@link #API_TOKEN} and {@link #API_SECRET} with your credentials and call {@link com.judopay.Judo#setup} to initialize the SDK.
 */
@SuppressWarnings("UnusedParameters")
public class MainActivity extends BaseActivity {

    private static final String AMOUNT = "0.99";

    private static final String JUDO_ID = "<JUDO_ID>";
    private static final String API_TOKEN = "<API_TOKEN>";
    private static final String API_SECRET = "<API_SECRET>";

    private static final String CONSUMER_REF = "AndroidSdkSampleConsumerRef";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Judo.setup(API_TOKEN, API_SECRET, Judo.SANDBOX);
        setConfiguration();
    }

    public void performPayment(View view) {
        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);

        Intent cardScanIntent = new Intent(this, CardScanningActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, cardScanIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        JudoOptions judoOptions = new JudoOptions.Builder()
                .setJudoId(JUDO_ID)
                .setAmount(AMOUNT)
                .setCurrency(getCurrency())
                .setConsumerRef(CONSUMER_REF)
                .setCardScanningIntent(pendingIntent)
                .setSecureServerMessageShown(true)
                .build();

        intent.putExtra(Judo.JUDO_OPTIONS, judoOptions);
        startActivityForResult(intent, PAYMENT_REQUEST);
    }

    public void performPreAuth(View view) {
        Intent intent = new Intent(MainActivity.this, PreAuthActivity.class);

        Intent cardScanIntent = new Intent(this, CardScanningActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, cardScanIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        JudoOptions options = new JudoOptions.Builder()
                .setJudoId(JUDO_ID)
                .setAmount(AMOUNT)
                .setCurrency(getCurrency())
                .setConsumerRef(CONSUMER_REF)
                .setCardScanningIntent(pendingIntent)
                .setSecureServerMessageShown(true)
                .build();

        intent.putExtra(Judo.JUDO_OPTIONS, options);
        startActivityForResult(intent, PRE_AUTH_REQUEST);
    }

    public void performRegisterCard(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterCardActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId(JUDO_ID)
                .setConsumerRef(CONSUMER_REF)
                .setSecureServerMessageShown(true)
                .build());

        startActivityForResult(intent, REGISTER_CARD_REQUEST);
    }

    public void performTokenPreAuth(View v) {
        Receipt receipt = getLastReceipt();
        if (receipt != null) {
            Intent intent = new Intent(MainActivity.this, TokenPreAuthActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                    .setJudoId(JUDO_ID)
                    .setAmount(AMOUNT)
                    .setCurrency(getCurrency())
                    .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .setSecureServerMessageShown(true)
                    .build());

            startActivityForResult(intent, TOKEN_PRE_AUTH_REQUEST);
        } else {
            Toast.makeText(MainActivity.this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    public void performTokenPayment(View view) {
        Receipt receipt = getLastReceipt();

        if (receipt != null) {
            Intent intent = new Intent(this, TokenPaymentActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                    .setJudoId(JUDO_ID)
                    .setAmount(AMOUNT)
                    .setCurrency(getCurrency())
                    .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .setSecureServerMessageShown(true)
                    .build());

            startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
        } else {
            Toast.makeText(MainActivity.this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrency() {
        return getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(CURRENCY_KEY, Currency.GBP);
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
        Intent intent = new Intent(MainActivity.this, TokenPaymentActivity.class);

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId(JUDO_ID)
                .setAmount(AMOUNT)
                .setCurrency(getCurrency())
                .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                .setCardToken(receipt.getCardDetails())
                .setSecureServerMessageShown(true)
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
        Judo.setAvsEnabled(settingsPrefs.isAvsEnabled());
        Judo.setMaestroEnabled(settingsPrefs.isMaestroEnabled());
        Judo.setAmexEnabled(settingsPrefs.isAmexEnabled());
    }

}