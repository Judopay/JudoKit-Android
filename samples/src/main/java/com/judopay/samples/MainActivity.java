package com.judopay.samples;

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
import com.judopay.customer.CardToken;
import com.judopay.payment.Payment;
import com.judopay.payment.PaymentActivity;
import com.judopay.payment.Receipt;
import com.judopay.payment.TokenPayment;
import com.judopay.payment.TokenPaymentActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.JudoPay.JUDO_RECEIPT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    @Bind(R.id.payment_button)
    View paymentButton;

    @Bind(R.id.pre_auth_button)
    View preAuthButton;

    @Bind(R.id.token_payment_button)
    View tokenPaymentButton;

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
                        .setAmount("1.99")
                        .setConsumer(new Consumer("yourConsumerRef"))
                        .setPaymentRef("paymentRef")
                        .build();

                intent.putExtra(JudoPay.EXTRA_PAYMENT, payment);
                intent.putExtra(Intent.EXTRA_TITLE, "Payment");

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        preAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                Payment payment = new Payment.Builder()
                        .setJudoId("100407196")
                        .setCurrency("GBP")
                        .setAmount("1.99")
                        .setConsumer(new Consumer("yourConsumerRef"))
                        .setPaymentRef("paymentRef")
                        .build();

                intent.putExtra(JudoPay.EXTRA_PAYMENT, payment);
                intent.putExtra(Intent.EXTRA_TITLE, "Payment");

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        tokenPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TokenPaymentActivity.class);
                TokenPayment tokenPayment = new TokenPayment.Builder()
                        .setCardToken(new CardToken("1215", "3436", "mAFOQ2hGGvyeyzcXKVwIujzWPRmag1hK", 1))
                        .setConsumer(new Consumer("consumerToken", "consumer100407196"))
                        .setPaymentReference("Judo_RegisterCardPreAuth_635833719833639217")
                        .setJudoId("100407196")
                        .setAmount("1.99")
                        .setCurrency("GBP")
                        .build();

                intent.putExtra(JudoPay.EXTRA_TOKEN_PAYMENT, tokenPayment);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case JudoPay.RESULT_PAYMENT_SUCCESS:
                    handlePaymentSuccess(data);
                    break;

                case JudoPay.RESULT_PAYMENT_DECLINED:
                    handlePaymentDeclined();
                    break;

                case JudoPay.RESULT_CANCELED:
                    Toast.makeText(MainActivity.this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                    break;

                case JudoPay.RESULT_ERROR:
                    Toast.makeText(MainActivity.this, "Payment response error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void handlePaymentDeclined() {
        new AlertDialog.Builder(this)
                .setTitle("Payment failed")
                .setMessage("Please check your details and try again")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void handlePaymentSuccess(Intent data) {
        Receipt receipt = data.getParcelableExtra(JUDO_RECEIPT);

        new AlertDialog.Builder(this)
                .setTitle("Payment successful")
                .setMessage(String.format("Receipt ID: %s", receipt.getReceiptId()))
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

}