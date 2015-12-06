package com.judopay;

import android.os.Bundle;

import com.judopay.model.Card;
import com.judopay.model.Location;
import com.judopay.model.PaymentTransaction;

import static com.judopay.BundleUtil.toMap;

class PaymentPresenter extends BasePaymentPresenter {

    public PaymentPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler) {
        super(view, judoApiService, scheduler);
    }

    public void performPayment(Card card, String consumerRef, String judoId, String amount, String currency, Bundle metaData, boolean threeDSecureEnabled) {
        this.loading = true;

        paymentFormView.showLoading();

        PaymentTransaction.Builder builder = new PaymentTransaction.Builder()
                .setAmount(amount)
                .setCardAddress(card.getCardAddress())
                .setConsumerLocation(new Location())
                .setCardNumber(card.getCardNumber())
                .setCurrency(currency)
                .setCv2(card.getCv2())
                .setJudoId(Long.valueOf(judoId))
                .setYourConsumerReference(consumerRef)
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
