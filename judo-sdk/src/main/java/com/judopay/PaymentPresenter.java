package com.judopay;

import android.os.Bundle;

import com.judopay.customer.Card;
import com.judopay.customer.Location;
import com.judopay.payment.Payment;

import static com.judopay.BundleUtil.toMap;

class PaymentPresenter extends BasePaymentPresenter {

    public PaymentPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler) {
        super(view, judoApiService, scheduler);
    }

    public void performPayment(Card card, Consumer consumer, String judoId, String amount, String currency, String paymentRef, Bundle metaData, boolean threeDSecureEnabled) {
        this.paymentInProgress = true;

        paymentFormView.showLoading();

        Payment.Builder builder = new Payment.Builder()
                .setAmount(amount)
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCardNumber(card.getCardNumber())
                .setCurrency(currency)
                .setCv2(card.getCv2())
                .setJudoId(Long.valueOf(judoId))
                .setYourConsumerReference(consumer.getYourConsumerReference())
                .setYourPaymentReference(paymentRef)
                .setExpiryDate(card.getExpiryDate());

        if (metaData != null) {
            builder.setMetaData(toMap(metaData));
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.payment(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(threeDSecureEnabled), error());
    }

}
