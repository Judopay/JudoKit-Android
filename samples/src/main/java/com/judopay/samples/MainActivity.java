package com.judopay.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.judopay.Consumer;
import com.judopay.JudoPay;
import com.judopay.payment.Payment;
import com.judopay.payment.PaymentActivity;

import static com.judopay.payment.PaymentActivity.JUDO_PAYMENT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JudoPay judoPay = new JudoPay();
        judoPay.initialise(this, "823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6");

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(JUDO_PAYMENT, new Payment.Builder()
                .setJudoId(100407196)
                .setCurrency("GBP")
                .setAmount(9.99f)
                .setConsumer(new Consumer())
                .setPaymentRef("paymentRef")
                .build());

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case JudoPay.SUCCESS:
                    Toast.makeText(MainActivity.this, "Payment response success", Toast.LENGTH_SHORT).show();
                    break;

                case JudoPay.ERROR:
                    Toast.makeText(MainActivity.this, "Payment response error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}