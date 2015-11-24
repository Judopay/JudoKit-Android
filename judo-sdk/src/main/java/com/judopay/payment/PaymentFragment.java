package com.judopay.payment;

import android.os.Bundle;

import com.judopay.Client;
import com.judopay.Consumer;
import com.judopay.JudoPay;
import com.judopay.customer.Card;
import com.judopay.customer.Location;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public class PaymentFragment extends BasePaymentFragment {

    public static PaymentFragment newInstance(Payment payment, PaymentListener listener) {
        PaymentFragment paymentFragment = new PaymentFragment();
        paymentFragment.paymentListener = listener;

        Bundle arguments = new Bundle();
        arguments.putParcelable(JudoPay.EXTRA_PAYMENT, payment);
        paymentFragment.setArguments(arguments);

        return paymentFragment;
    }

    @Override
    public void onSubmit(Card card, boolean threeDSecureEnabled) {
        if (!getArguments().containsKey(EXTRA_PAYMENT)) {
            throw new RuntimeException("Payment extra must be provided to PaymentFragment");
        }

        Payment payment = getArguments().getParcelable(EXTRA_PAYMENT);

        PaymentTransaction.Builder builder = new PaymentTransaction.Builder()
                .setAmount(payment.getAmount())
                .setCardAddress(card.getCardAddress())
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

        performPayment(builder.build());
    }

    private void performPayment(PaymentTransaction paymentTransaction) {
        onLoadStarted();
        judoApiService.payment(paymentTransaction)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

}