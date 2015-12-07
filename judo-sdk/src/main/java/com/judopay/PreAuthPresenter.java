package com.judopay;

import android.os.Bundle;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.Consumer;
import com.judopay.model.Location;
import com.judopay.model.PaymentTransaction;
import com.judopay.model.Receipt;

import rx.functions.Action1;

import static com.judopay.BundleUtil.toMap;

class PreAuthPresenter extends BasePaymentPresenter {

    public PreAuthPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(view, judoApiService, scheduler, gson);
    }

    public void performPreAuth(Card card, String consumerRef, String judoId, String amount, String currency, Bundle metaData, boolean threeDSecureEnabled) {
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

        apiService.preAuth(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(threeDSecureEnabled), error());
    }

}
