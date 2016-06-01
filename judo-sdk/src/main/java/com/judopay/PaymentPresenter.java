package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.TokenRequest;

class PaymentPresenter extends BasePresenter {

    public PaymentPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(callbacks, judoApiService, scheduler, gson);
    }

    public void performPayment(Card card, Judo judo) {
        this.loading = true;

        transactionCallbacks.showLoading();

        PaymentRequest.Builder builder = new PaymentRequest.Builder()
                .setAmount(judo.getAmount())
                .setCardAddress(card.getCardAddress())
                .setCardNumber(card.getCardNumber())
                .setCurrency(judo.getCurrency())
                .setCv2(card.getSecurityCode())
                .setJudoId(judo.getJudoId())
                .setYourConsumerReference(judo.getConsumerRef())
                .setExpiryDate(card.getExpiryDate())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.payment(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

    public void performTokenPayment(Card card, Judo judo) {
        this.loading = true;
        transactionCallbacks.showLoading();

        TokenRequest tokenRequest = new TokenRequest.Builder()
                .setAmount(judo.getAmount())
                .setCardAddress(card.getCardAddress())
                .setCurrency(judo.getCurrency())
                .setJudoId(judo.getJudoId())
                .setYourConsumerReference(judo.getConsumerRef())
                .setCv2(card.getSecurityCode())
                .setToken(judo.getCardToken())
                .setMetaData(judo.getMetaDataMap())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .build();

        apiService.tokenPayment(tokenRequest)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}