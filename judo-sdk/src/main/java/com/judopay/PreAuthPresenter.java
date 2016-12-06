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

    public void performPreAuth(Card card, Judo judo) {
        this.loading = true;
        transactionCallbacks.showLoading();

        PaymentRequest.Builder builder = new PaymentRequest.Builder()
                .setAmount(judo.getAmount())
                .setCardNumber(card.getCardNumber())
                .setCurrency(judo.getCurrency())
                .setCv2(card.getSecurityCode())
                .setJudoId(judo.getJudoId())
                .setYourConsumerReference(judo.getConsumerReference())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap())
                .setExpiryDate(card.getExpiryDate());

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
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


    public void performTokenPreAuth(Card card, Judo judo) {
        this.loading = true;
        transactionCallbacks.showLoading();

        TokenRequest.Builder builder = new TokenRequest.Builder()
                .setAmount(judo.getAmount())
                .setCurrency(judo.getCurrency())
                .setJudoId(judo.getJudoId())
                .setYourConsumerReference(judo.getConsumerReference())
                .setCv2(card.getSecurityCode())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap())
                .setToken(judo.getCardToken());

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        apiService.tokenPreAuth(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}