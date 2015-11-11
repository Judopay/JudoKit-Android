package com.judopay.payment;

import android.os.Bundle;

import com.judopay.Client;
import com.judopay.JudoPay;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.customer.Location;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PreAuthFragment extends BasePaymentFragment {

    public static PreAuthFragment newInstance(Payment payment, PaymentListener listener) {
        PreAuthFragment preAuthFragment = new PreAuthFragment();
        preAuthFragment.paymentListener = listener;

        Bundle arguments = new Bundle();
        arguments.putParcelable(JudoPay.EXTRA_PAYMENT, payment);
        preAuthFragment.setArguments(arguments);

        return preAuthFragment;
    }

    @Override
    public void onSubmit(Card card) {
        Payment payment = getArguments().getParcelable(EXTRA_PAYMENT);

        PaymentTransaction.Builder builder = new PaymentTransaction.Builder()
                .setAmount(payment.getAmount())
                .setCardAddress(new Address.Builder()
                        .setPostCode(card.getCardAddress().getPostcode())
                        .build())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCardNumber(card.getCardNumber())
                .setCurrency(payment.getCurrency())
                .setCv2(card.getCv2())
                .setJudoId(Long.valueOf(payment.getJudoId()))
                .setYourConsumerReference(payment.getConsumer().getYourConsumerReference())
                .setYourPaymentReference(payment.getPaymentRef())
                .setExpiryDate(card.getExpiryDate());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        performPreAuth(builder.build());
    }

    private void performPreAuth(PaymentTransaction paymentTransaction) {
        onLoadStarted();
        judoApiService.preAuth(paymentTransaction)
                .subscribe(this);
    }

}