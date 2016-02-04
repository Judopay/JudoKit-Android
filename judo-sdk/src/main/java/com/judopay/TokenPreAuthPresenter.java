package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.TokenTransaction;

class TokenPreAuthPresenter extends BasePresenter {

    public TokenPreAuthPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(view, judoApiService, scheduler, gson);
    }

    public void performTokenPreAuth(Card card, JudoOptions options) {
        this.loading = true;
        paymentFormView.showLoading();

        TokenTransaction tokenTransaction = new TokenTransaction.Builder()
                .setAmount(options.getAmount())
                .setCardAddress(card.getCardAddress())
                .setConsumerLocation(null)
                .setCurrency(options.getCurrency())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setCv2(card.getCv2())
                .setMetaData(options.getMetaDataMap())
                .setEndDate(options.getCardToken().getEndDate())
                .setLastFour(options.getCardToken().getLastFour())
                .setToken(options.getCardToken().getToken())
                .setType(options.getCardToken().getType())
                .build();

        apiService.tokenPreAuth(tokenTransaction)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}