package com.judopay.samples;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.TokenPaymentActivity;
import com.judopay.TokenPreAuthActivity;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.view.Dialogs;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.Judo.JUDO_RECEIPT;

/**
 * Sample app screen containing buttons to activate the different features of the Judo SDK
 * <br>
 * Update the {@link #JUDO_ID} string with the Judo ID from the judo website: http://www.judopay.com,
 * Update the {@link #API_TOKEN} and {@link #API_SECRET} with your credentials and call {@link com.judopay.Judo#setup} to initialize the SDK.
 */
public class MainActivity extends BaseActivity {

    // Constants to define different actions (for use with startActivityForResult(...))
    private static final int PAYMENT_REQUEST = 101;
    private static final int TOKEN_PAYMENT_REQUEST = 102;
    private static final int PRE_AUTH_REQUEST = 201;
    private static final int TOKEN_PRE_AUTH_REQUEST = 202;
    private static final int REGISTER_CARD_REQUEST = 301;

    private static final String AMOUNT = "0.99";
    private static final String JUDO_ID = "00000000";

    private static final String API_TOKEN = "sampleApiToken";
    private static final String API_SECRET = "sampleApiSecret";
    private static final String CONSUMER_REF = "consumerRef";

    @Bind(R.id.payment_button)
    View paymentButton;

    @Bind(R.id.pre_auth_button)
    View preAuthButton;

    @Bind(R.id.token_payment_button)
    View tokenPaymentButton;

    @Bind(R.id.token_pre_auth_button)
    View tokenPreAuthButton;

    @Bind(R.id.add_card_button)
    View addCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Judo.setup(API_TOKEN, API_SECRET, Judo.Environment.SANDBOX);


        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);

                JudoOptions judoOptions = new JudoOptions.Builder()
                        .setJudoId(JUDO_ID)
                        .setAmount(AMOUNT)
                        .setCurrency(getCurrency())
                        .setConsumerRef(CONSUMER_REF)
                        .build();

                intent.putExtra(Judo.JUDO_OPTIONS, judoOptions);
                startActivityForResult(intent, PAYMENT_REQUEST);
            }
        });

        preAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreAuthActivity.class);

                JudoOptions options = new JudoOptions.Builder()
                        .setJudoId(JUDO_ID)
                        .setAmount(AMOUNT)
                        .setCurrency(getCurrency())
                        .setConsumerRef(CONSUMER_REF)
                        .build();

                intent.putExtra(Judo.JUDO_OPTIONS, options);
                startActivityForResult(intent, PRE_AUTH_REQUEST);
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
                Intent intent = new Intent(MainActivity.this, RegisterCardActivity.class);
                intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                        .setJudoId(JUDO_ID)
                        .setConsumerRef(CONSUMER_REF)
                        .build());

                startActivityForResult(intent, REGISTER_CARD_REQUEST);
            }
        });
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

    private void attemptTokenPreAuth() {
        Receipt receipt = getLastReceipt();
        if (receipt != null) {
            Intent intent = new Intent(MainActivity.this, TokenPreAuthActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                    .setJudoId(JUDO_ID)
                    .setAmount(AMOUNT)
                    .setCurrency(getCurrency())
                    .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .build());

            startActivityForResult(intent, TOKEN_PRE_AUTH_REQUEST);
        } else {
            Toast.makeText(MainActivity.this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    protected void attemptTokenPayment() {
        Receipt receipt = getLastReceipt();

        if (receipt != null) {
            Intent intent = new Intent(this, TokenPaymentActivity.class);

            intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                    .setJudoId(JUDO_ID)
                    .setAmount(AMOUNT)
                    .setCurrency(getCurrency())
                    .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                    .setCardToken(receipt.getCardDetails())
                    .build());

            startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
        } else {
            Toast.makeText(MainActivity.this, R.string.add_card_to_make_token_transaction, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegisterCardResult(int resultCode, Intent data) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);

                saveReceipt(receipt);

                showTokenPaymentDialog(receipt);
                break;

            case Judo.RESULT_DECLINED:
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

            case Judo.RESULT_DECLINED:
                Dialogs.createDeclinedPaymentDialog(this).show();
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

}