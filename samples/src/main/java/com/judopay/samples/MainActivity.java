package com.judopay.samples;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.judopay.Dialogs;
import com.judopay.JudoActivity;
import com.judopay.JudoPay;
import com.judopay.RegisterCardActivity;
import com.judopay.TokenPaymentActivity;
import com.judopay.model.CardToken;
import com.judopay.model.Receipt;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.JudoPay.Environment.SANDBOX;
import static com.judopay.JudoPay.JUDO_RECEIPT;

/**
 * Sample app screen containing buttons to activate the different features of the Judo SDK
 * <p/>
 * Update the MY_JUDO_ID string with the Judo ID from the judo website: http://www.judopay.com,
 * In the SampleApp, you will also need to update the token and secret with the values for your account.
 */
public class MainActivity extends AppCompatActivity {

    private static final String MY_AMOUNT = "1.99";
    private static final String MY_JUDO_ID = "100407196";

    // Constants to define different actions (for use with startActivityForResult(...))
    private static final int PAYMENT_REQUEST = 101;
    private static final int TOKEN_PAYMENT_REQUEST = 102;
    private static final int PRE_AUTH_REQUEST = 201;
    private static final int TOKEN_PRE_AUTH_REQUEST = 202;
    private static final int REGISTER_CARD_REQUEST = 301;

    static final String SHARED_PREFS_NAME = "Judo-SampleApp";
    static final String CURRENCY_KEY = "Judo-SampleApp-Currency";
    private static final String TOKEN_RECEIPT_KEY = "Judo-SampleApp-TokenReceipt";

    @Bind(R.id.payment_button)
    private View paymentButton;

    @Bind(R.id.pre_auth_button)
    private View preAuthButton;

    @Bind(R.id.token_payment_button)
    private View tokenPaymentButton;

    @Bind(R.id.token_pre_auth_button)
    private View tokenPreAuthButton;

    @Bind(R.id.add_card_button)
    private View addCardButton;

    private String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        JudoPay.setup(this, "823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6", SANDBOX);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JudoActivity.startPaymentActivity(MainActivity.this, PAYMENT_REQUEST, MY_JUDO_ID, MY_AMOUNT, currency, "yourConsumerRef", null);
            }
        });

        preAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JudoActivity.startPreAuthActivity(MainActivity.this, PRE_AUTH_REQUEST, MY_JUDO_ID, MY_AMOUNT, currency, "yourConsumerRef", null);
            }
        });

        tokenPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptTokenPayment();
            }
        });

        tokenPreAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptTokenPreAuth();
            }
        });

        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterCardActivity(MainActivity.this, "consumerToken", REGISTER_CARD_REQUEST);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.currency = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(CURRENCY_KEY, "GBP");
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

    private void attemptTokenPreAuth() {
        Receipt receipt = getLastReceipt();
        if (receipt != null) {
            JudoActivity.startTokenPreAuthActivity(MainActivity.this, TOKEN_PRE_AUTH_REQUEST,
                    MY_JUDO_ID, MY_AMOUNT, currency, receipt.getConsumer().getYourConsumerReference(), receipt.getCardDetails(), null);
        } else {
            Toast.makeText(MainActivity.this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    protected void attemptTokenPayment() {
        Receipt receipt = getLastReceipt();

        if (receipt != null) {
            Intent intent = getTokenPaymentIntent(currency, receipt.getConsumer().getYourConsumerReference(),
                    receipt.getCardDetails(), MY_JUDO_ID, null, MY_AMOUNT);
            startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
        } else {
            Toast.makeText(MainActivity.this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    private Receipt getLastReceipt() {
        String tokenReceiptJson = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(TOKEN_RECEIPT_KEY, null);

        if (tokenReceiptJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(tokenReceiptJson, Receipt.class);
        }
        return null;
    }

    private void startRegisterCardActivity(Context context, String consumer, int requestCode) {
        if (context != null) {
            Intent intent = new Intent(context, RegisterCardActivity.class);
            intent.putExtra(JudoPay.JUDO_CONSUMER, consumer);
            intent.putExtra(JudoPay.JUDO_ID, MY_JUDO_ID);

            startActivityForResult(intent, requestCode);
        }
    }

    private void handleRegisterCardResult(int resultCode, Intent data) {
        switch (resultCode) {
            case JudoPay.RESULT_SUCCESS:
                Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);

                getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putString(TOKEN_RECEIPT_KEY, new Gson().toJson(receipt))
                        .apply();

                showTokenPaymentDialog(receipt);
                break;

            case JudoPay.RESULT_DECLINED:
                Dialogs.createDeclinedPaymentDialog(this).show();
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
                        Intent intent = getTokenPaymentIntent(currency, receipt.getConsumer().getYourConsumerReference(), receipt.getCardDetails(), MY_JUDO_ID, new Bundle(), MY_AMOUNT);
                        startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private Intent getTokenPaymentIntent(String currency, String consumer, CardToken cardDetails, String judoId, Bundle metaData, String amount) {
        Intent intent = new Intent(MainActivity.this, TokenPaymentActivity.class);

        intent.putExtra(JudoPay.JUDO_ID, judoId);
        intent.putExtra(JudoPay.JUDO_AMOUNT, amount);
        intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
        intent.putExtra(JudoPay.JUDO_CONSUMER, consumer);
        intent.putExtra(JudoPay.JUDO_META_DATA, metaData);
        intent.putExtra(JudoPay.JUDO_CARD_TOKEN, cardDetails);

        return intent;
    }

    private void handleResult(int resultCode, Intent data) {
        switch (resultCode) {
            case JudoPay.RESULT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                Toast.makeText(MainActivity.this, "Success: " + response.getReceiptId(), Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_DECLINED:
                Dialogs.createDeclinedPaymentDialog(this).show();
                break;

            case JudoPay.RESULT_ERROR:
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

}