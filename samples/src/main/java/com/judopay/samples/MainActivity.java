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

import com.judopay.Consumer;
import com.judopay.JudoPay;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.RegisterCardActivity;
import com.judopay.TokenPaymentActivity;
import com.judopay.customer.CardToken;
import com.judopay.payment.Receipt;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.JudoPay.JUDO_CONSUMER;
import static com.judopay.JudoPay.JUDO_RECEIPT;

/**
 * Sample app screen containing buttons to activate the different features of the Judo SDK
 * <p/>
 * Update the MY_JUDO_ID string with the Judo ID from the judo website: http://www.judopay.com,
 * In the SampleApp, you will also need to update the token and secret with the values for your account.
 */
public class MainActivity extends AppCompatActivity {

    // Constants to define different actions (for use with startActivityForResult(...))
    private static final int PAYMENT_REQUEST = 101;
    private static final int TOKEN_PAYMENT_REQUEST = 102;
    private static final int PRE_AUTH_REQUEST = 201;
    private static final int TOKEN_PRE_AUTH_REQUEST = 202;
    private static final int REGISTER_CARD_REQUEST = 301;
    private static final int REGISTER_CARD_TOKEN_PAYMENT_REQUEST = 501;
    private static final int REGISTER_CARD_TOKEN_PRE_AUTH_REQUEST = 601;

    private static final String MY_AMOUNT = "1.99";
    private static final String MY_JUDO_ID = "100407196";

    @Bind(R.id.payment_button)
    View paymentButton;

    @Bind(R.id.pre_auth_button)
    View preAuthButton;

    @Bind(R.id.token_payment_button)
    View tokenPaymentButton;

    @Bind(R.id.register_card_button)
    View registerCardButton;

    private String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initialiseView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.currency = getSharedPreferences(SampleApp.SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(SampleApp.CURRENCY_KEY, "GBP");
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

    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void initialiseView() {
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);

                intent.putExtra(JudoPay.JUDO_ID, MY_JUDO_ID);
                intent.putExtra(JudoPay.JUDO_AMOUNT, MY_AMOUNT);
                intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
                intent.putExtra(JudoPay.JUDO_CONSUMER, new Consumer("yourConsumerRef"));
                intent.putExtra(JudoPay.JUDO_PAYMENT_REF, "paymentRef");
                intent.putExtra(JudoPay.JUDO_META_DATA, new Bundle());

                startActivityForResult(intent, PAYMENT_REQUEST);
            }
        });

        preAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreAuthActivity.class);
                intent.putExtra(JudoPay.JUDO_ID, MY_JUDO_ID);
                intent.putExtra(JudoPay.JUDO_AMOUNT, MY_AMOUNT);
                intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
                intent.putExtra(JudoPay.JUDO_CONSUMER, new Consumer("yourConsumerRef"));
                intent.putExtra(JudoPay.JUDO_PAYMENT_REF, "paymentRef");
                intent.putExtra(JudoPay.JUDO_META_DATA, new Bundle());

                startActivityForResult(intent, PAYMENT_REQUEST);
            }
        });

        tokenPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consumer consumer = new Consumer("zGEHXkSTZO08FljI", "consumer10102");
                CardToken cardToken = new CardToken("1215", "6208", "P96xnOZjsJDdwEYgHZSbVB70A4U1VLz7", 0);
                Intent intent = getTokenPaymentIntent(currency, consumer, "payment1010102", cardToken, MY_JUDO_ID, null, MY_AMOUNT);

                startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
            }
        });

        registerCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consumer consumer = new Consumer("consumerToken", UUID.randomUUID().toString());
                startRegisterCardActivity(MainActivity.this, consumer, REGISTER_CARD_REQUEST);
            }
        });
    }

    private void startRegisterCardActivity(Context context, Consumer consumer, int requestCode) {
        if (context != null) {
            Intent intent = new Intent(context, RegisterCardActivity.class);
            intent.putExtra(JUDO_CONSUMER, consumer);
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PAYMENT_REQUEST:
                handlePaymentResult(resultCode, data);
                break;

            case TOKEN_PAYMENT_REQUEST:
                handleTokenPaymentResult(resultCode, data);
                break;

            case PRE_AUTH_REQUEST:
                handlePreAuthResult(resultCode, data);
                break;

            case REGISTER_CARD_REQUEST:
                handleRegisterCardResult(resultCode, data);
                break;
        }
    }

    private void handleRegisterCardResult(int resultCode, Intent data) {
        switch (resultCode) {
            case JudoPay.RESULT_REGISTER_CARD_SUCCESS:
                Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);
                showRegisteredCardDialog(receipt);
                break;

            case JudoPay.RESULT_REGISTER_CARD_DECLINED:
                Toast.makeText(MainActivity.this, "Register card declined", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showRegisteredCardDialog(final Receipt receipt) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.make_token_payment))
                .setMessage(getString(R.string.registered_card_can_perform_token_payments))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getTokenPaymentIntent(currency, receipt.getConsumer(), receipt.getYourPaymentReference(), receipt.getCardDetails(), MY_JUDO_ID, new Bundle(), MY_AMOUNT);

                        startActivityForResult(intent, TOKEN_PAYMENT_REQUEST);
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private Intent getTokenPaymentIntent(String currency, Consumer consumer, String yourPaymentReference, CardToken cardDetails, String judoId, Bundle metaData, String amount) {
        Intent intent = new Intent(MainActivity.this, TokenPaymentActivity.class);

        intent.putExtra(JudoPay.JUDO_ID, judoId);
        intent.putExtra(JudoPay.JUDO_AMOUNT, amount);
        intent.putExtra(JudoPay.JUDO_CURRENCY, currency);
        intent.putExtra(JudoPay.JUDO_CONSUMER, consumer);
        intent.putExtra(JudoPay.JUDO_PAYMENT_REF, yourPaymentReference);
        intent.putExtra(JudoPay.JUDO_META_DATA, metaData);
        intent.putExtra(JudoPay.JUDO_CARD_TOKEN, cardDetails);

        return intent;
    }

    private void handlePreAuthResult(int resultCode, Intent data) {
        switch (resultCode) {
            case JudoPay.RESULT_PAYMENT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                Toast.makeText(MainActivity.this, "Pre-auth success: " + response.getReceiptId(), Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_PAYMENT_DECLINED:
                Toast.makeText(MainActivity.this, "Pre-auth declined", Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_CANCELED:
                Toast.makeText(MainActivity.this, "Pre-auth cancelled", Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_ERROR:
                Toast.makeText(MainActivity.this, "Pre-auth error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleTokenPaymentResult(int resultCode, Intent data) {
        switch (resultCode) {
            case JudoPay.RESULT_PAYMENT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                Toast.makeText(MainActivity.this, "Token payment success: " + response.getReceiptId(), Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_PAYMENT_DECLINED:
                Toast.makeText(MainActivity.this, "Token payment declined", Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_CANCELED:
                Toast.makeText(MainActivity.this, "Token payment cancelled", Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_ERROR:
                Toast.makeText(MainActivity.this, "Token payment response error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handlePaymentResult(int resultCode, Intent data) {
        switch (resultCode) {
            case JudoPay.RESULT_PAYMENT_SUCCESS:
                Receipt response = data.getParcelableExtra(JUDO_RECEIPT);
                Toast.makeText(MainActivity.this, "Payment success: " + response.getReceiptId(), Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_PAYMENT_DECLINED:
                Toast.makeText(MainActivity.this, "Payment declined", Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_CANCELED:
                Toast.makeText(MainActivity.this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                break;

            case JudoPay.RESULT_ERROR:
                Toast.makeText(MainActivity.this, "Payment error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}