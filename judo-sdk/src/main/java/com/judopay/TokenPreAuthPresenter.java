package com.judopay;

import android.os.Bundle;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Location;
import com.judopay.model.TokenTransaction;

import static com.judopay.BundleUtil.toMap;

class TokenPreAuthPresenter extends BasePresenter {

    public TokenPreAuthPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(view, judoApiService, scheduler, gson);
    }

    public void performTokenPreAuth(Card card, CardToken cardToken, String consumerRef, String judoId, String amount, String currency, Bundle metaData, boolean threeDSecureEnabled) {
        this.loading = true;
        paymentFormView.showLoading();

        TokenTransaction tokenTransaction = new TokenTransaction.Builder()
                .setAmount(amount)
                .setCardAddress(card.getCardAddress())
                .setConsumerLocation(new Location())
                .setCurrency(currency)
                .setJudoId(Long.valueOf(judoId))
                .setYourConsumerReference(consumerRef)
                .setCv2(card.getCv2())
                .setMetaData(toMap(metaData))
                .setEndDate(cardToken.getEndDate())
                .setLastFour(cardToken.getLastFour())
                .setToken(cardToken.getToken())
                .setType(cardToken.getType())
                .build();

        apiService.tokenPreAuth(tokenTransaction)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(threeDSecureEnabled), error());
    }

}