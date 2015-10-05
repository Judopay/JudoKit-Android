package com.judopay.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.judopay.Consumer;
import com.judopay.JudoPay;
import com.judopay.payment.Payment;
import com.judopay.payment.PaymentActivity;
import com.judopay.payment.PaymentResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.judopay.JudoPay.Environment.SANDBOX;
import static com.judopay.JudoPay.JUDO_PAYMENT;
import static com.judopay.JudoPay.JUDO_RECEIPT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    @Bind(R.id.payment_button)
    Button paymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        JudoPay.setup(this, "823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6", SANDBOX);
        JudoPay.setAvsEnabled(true);

        initialiseView();
    }

    private void initialiseView() {
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);

                Payment payment = new Payment.Builder()
                        .setJudoId(100407196)
                        .setCurrency("GBP")
                        .setAmount(9.99f)
                        .setConsumer(new Consumer("yourConsumerRef"))
                        .setPaymentRef("paymentRef")
                        .build();

                intent.putExtra(JUDO_PAYMENT, payment);

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
                    PaymentResponse response = data.getParcelableExtra(JUDO_RECEIPT);
                    Toast.makeText(MainActivity.this, "Payment response success: " + response.getReceiptId(), Toast.LENGTH_SHORT).show();
                    break;

                case JudoPay.RESULT_ERROR:
                    Toast.makeText(MainActivity.this, "Payment response error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}