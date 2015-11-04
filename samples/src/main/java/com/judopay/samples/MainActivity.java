package com.judopay.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.judopay.Consumer;
import com.judopay.payment.PaymentActivity;
import com.judopay.JudoPay;
import com.judopay.payment.Payment;
import com.judopay.payment.PaymentResponse;
import com.judopay.payment.SingleClickOnClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.JudoPay.JUDO_RECEIPT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    @Bind(R.id.payment_1_pound_button)
    Button pay1PoundButton;

    @Bind(R.id.payment_2_pound_button)
    Button pay2PoundButton;

    @Bind(R.id.payment_5_pound_button)
    Button pay5PoundButton;

    @Bind(R.id.payment_10_pound_button)
    Button pay10PoundButton;

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
        pay1PoundButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick(View v) {
                performPayment(1.00f);
            }
        });

        pay2PoundButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick(View v) {
                performPayment(2.00f);
            }
        });

        pay5PoundButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick(View v) {
                performPayment(5.00f);
            }
        });

        pay10PoundButton.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick(View v) {
                performPayment(10.00f);
            }
        });
    }

    private void performPayment(float amount) {
        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
        Payment payment = new Payment.Builder()
                .setJudoId(100347582)
                .setCurrency("GBP")
                .setAmount(amount)
                .setConsumer(new Consumer("yourConsumerRef"))
                .setPaymentRef("paymentRef")
                .build();

        intent.putExtra(JudoPay.EXTRA_PAYMENT, payment);
        intent.putExtra(Intent.EXTRA_TITLE, "Payment");

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case JudoPay.RESULT_PAYMENT_SUCCESS:
                    PaymentResponse response = data.getParcelableExtra(JUDO_RECEIPT);
                    Toast.makeText(MainActivity.this, "Payment response success: " + response.getReceiptId(), Toast.LENGTH_SHORT).show();
                    break;

                case JudoPay.RESULT_PAYMENT_DECLINED:
                    Toast.makeText(MainActivity.this, "Payment declined", Toast.LENGTH_SHORT).show();
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

}