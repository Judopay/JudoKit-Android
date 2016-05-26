package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.TokenRequest;

class PreAuthPresenter extends BasePresenter {

    public PreAuthPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(callbacks, judoApiService, scheduler, gson);
    }

    public void performPreAuth(Card card, Judo options) {
        this.loading = true;

        transactionCallbacks.showLoading();

        PaymentRequest.Builder builder = new PaymentRequest.Builder()
                .setAmount(options.getAmount())
                .setCardAddress(card.getCardAddress())
                .setCardNumber(card.getCardNumber())
                .setCurrency(options.getCurrency())
                .setCv2(card.getSecurityCode())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .setMetaData(options.getMetaDataMap())
                .setExpiryDate(card.getExpiryDate());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.preAuth(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }


    public void performTokenPreAuth(Card card, Judo options) {
        this.loading = true;
        transactionCallbacks.showLoading();

        TokenRequest tokenRequest = new TokenRequest.Builder()
                .setAmount(options.getAmount())
                .setCardAddress(card.getCardAddress())
                .setCurrency(options.getCurrency())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setCv2(card.getSecurityCode())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .setMetaData(options.getMetaDataMap())
                .setToken(options.getCardToken())
                .build();

        apiService.tokenPreAuth(tokenRequest)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}
