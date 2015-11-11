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
import android.widget.Button;
import android.widget.Toast;

import com.judopay.Consumer;
import com.judopay.JudoPay;
import com.judopay.customer.CardToken;
import com.judopay.payment.Payment;
import com.judopay.payment.PaymentActivity;
import com.judopay.payment.Receipt;
import com.judopay.payment.TokenPayment;
import com.judopay.payment.TokenPaymentActivity;
import com.judopay.register.RegisterCardActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.JudoPay.JUDO_RECEIPT;

public class MainActivity extends AppCompatActivity {

    // Constants to define different actions (for use with startActivityForResult(...))
    private static final int PAYMENT_REQUEST = 101;
    private static final int TOKEN_PAYMENT_REQUEST = 102;
    private static final int PRE_AUTH_REQUEST = 201;
    private static final int TOKEN_PRE_AUTH_REQUEST = 202;
    private static final int REGISTER_CARD_REQUEST = 301;
    private static final int REGISTER_CARD_TOKEN_PAYMENT_REQUEST = 501;
    private static final int REGISTER_CARD_TOKEN_PRE_AUTH_REQUEST = 601;

    @Bind(R.id.payment_button)
    Button paymentButton;

    @Bind(R.id.pre_auth_button)
    Button preAuthButton;

    @Bind(R.id.token_payment_button)
    Button tokenPaymentButton;

    @Bind(R.id.register_card_button)
    Button registerCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initialiseView();
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
                Payment payment = new Payment.Builder()
                        .setJudoId("100407196")
                        .setCurrency("GBP")
                        .setAmount("9.99")
                        .setConsumer(new Consumer("yourConsumerRef"))
                        .setPaymentRef("paymentRef")
                        .build();

                intent.putExtra(JudoPay.EXTRA_PAYMENT, payment);
                intent.putExtra(Intent.EXTRA_TITLE, "Payment");

                startActivityForResult(intent, PAYMENT_REQUEST);
            }
        });

        preAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreAuthPaymentActivity(MainActivity.this, new Payment.Builder()
                        .setJudoId("100407196")
                        .setCurrency("GBP")
                        .setAmount("9.99")
                        .setConsumer(new Consumer("yourConsumerRef"))
                        .setPaymentRef("paymentRef")
                        .build(), PRE_AUTH_REQUEST);
            }
        });

        tokenPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenPayment tokenPayment = new TokenPayment.Builder()
                        .setCardToken(new CardToken("1215", "3436", "PKMxI4788SERKz0w7opSubZGU0I5g8kb", 1))
                        .setConsumer(new Consumer("zGEHXkSTZO08FljI", "consumer10102"))
                        .setPaymentReference("payment1010102")
                        .setJudoId("100016")
                        .setAmount("4.99")
                        .setCurrency("GBP")
                        .build();

                startTokenPaymentActivity(MainActivity.this, tokenPayment, TOKEN_PAYMENT_REQUEST);
            }
        });

        registerCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Consumer consumer = new Consumer("consumerToken", "consumer10102");
                startRegisterCardActivity(MainActivity.this, consumer, REGISTER_CARD_REQUEST);
            }
        });
    }

    private void startPreAuthPaymentActivity(Context context, Payment payment, int requestCode) {
        if (context != null) {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(JudoPay.EXTRA_PAYMENT, payment);

            startActivityForResult(intent, requestCode);
        }
    }

    private void startRegisterCardActivity(Context context, Consumer consumer, int requestCode) {
        if (context != null) {
            Intent intent = new Intent(context, RegisterCardActivity.class);
            intent.putExtra(RegisterCardActivity.EXTRA_CONSUMER, consumer);
            startActivityForResult(intent, requestCode);
        }
    }

    public void startTokenPaymentActivity(Context context, TokenPayment tokenPayment, int requestCode) {
        if (context != null) {
            Intent intent = new Intent(context, TokenPaymentActivity.class);
            intent.putExtra(TokenPaymentActivity.EXTRA_TOKEN_PAYMENT, tokenPayment);

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
                .setTitle("Make token payment?")
                .setMessage("Registered card can now be used to perform token payments")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TokenPayment tokenPayment = new TokenPayment.Builder()
                                .setCardToken(receipt.getCardDetails())
                                .setConsumer(receipt.getConsumer())
                                .setPaymentReference(receipt.getYourPaymentReference())
                                .setJudoId("100016")
                                .setAmount("4.99")
                                .setCurrency("GBP")
                                .build();

                        startTokenPaymentActivity(MainActivity.this, tokenPayment, TOKEN_PAYMENT_REQUEST);
                    }
                })
                .setNegativeButton("No", null)
                .show();
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