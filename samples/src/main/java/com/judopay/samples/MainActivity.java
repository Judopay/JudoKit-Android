package com.judopay.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.judopay.Client;
import com.judopay.JudoPay;
import com.judopay.customer.Address;
import com.judopay.customer.Location;
import com.judopay.payment.PaymentService;
import com.judopay.payment.Transaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.judopay.samples.R.layout.activity_main);

        JudoPay judoPay = new JudoPay();
        judoPay.initialise(this, "823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6");

        final PaymentService paymentService = new PaymentService();

        findViewById(R.id.payment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction.Builder builder = new Transaction.Builder()
                        .setAmount("9.99")
                        .setCardAddress(new Address.Builder()
                                .setPostCode("SW16 6AT")
                                .build())
                        .setClientDetails(new Client())
                        .setConsumerLocation(new Location())
                        .setCardNumber("4976000000003436")
                        .setCurrency("GBP")
                        .setCv2("452")
                        .setJudoId(100407196)
                        .setYourConsumerReference("consumerRef")
                        .setYourPaymentReference("paymentRef")
                        .setExpiryDate("12/15");

                paymentService.payment(builder.build());
            }
        });
    }

}
