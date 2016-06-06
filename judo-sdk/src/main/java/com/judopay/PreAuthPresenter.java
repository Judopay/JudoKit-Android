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

    public void performPreAuth(Card card, JudoOptions options) {
        this.loading = true;

        transactionCallbacks.showLoading();

        PaymentRequest.Builder builder = new PaymentRequest.Builder()
                .setAmount(options.getAmount())
                .setCardNumber(card.getCardNumber())
                .setCurrency(options.getCurrency())
                .setCv2(card.getSecurityCode())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .setMetaData(options.getMetaDataMap())
                .setExpiryDate(card.getExpiryDate());

        if(card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(options.getAddress());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.preAuth(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }


    public void performTokenPreAuth(Card card, JudoOptions options) {
        this.loading = true;
        transactionCallbacks.showLoading();

        TokenRequest.Builder builder = new TokenRequest.Builder()
                .setAmount(options.getAmount())
                .setCurrency(options.getCurrency())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setCv2(card.getSecurityCode())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .setMetaData(options.getMetaDataMap())
                .setToken(options.getCardToken());

        if(card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(options.getAddress());
        }

        TokenRequest tokenRequest = builder.build();

        apiService.tokenPreAuth(tokenRequest)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}