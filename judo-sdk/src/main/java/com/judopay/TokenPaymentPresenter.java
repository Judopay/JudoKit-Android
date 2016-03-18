package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.TokenRequest;

import java.math.BigDecimal;

class TokenPaymentPresenter extends BasePresenter {

    public TokenPaymentPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(callbacks, judoApiService, scheduler, gson);
    }

    public void performTokenPayment(Card card, JudoOptions options) {
        this.loading = true;

        transactionCallbacks.showLoading();

        TokenRequest tokenTransaction = new TokenRequest.Builder()
                .setAmount(new BigDecimal(options.getAmount()))
                .setCardAddress(card.getCardAddress())
                .setCurrency(options.getCurrency())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setCv2(card.getSecurityCode())
                .setToken(options.getCardToken())
                .setMetaData(options.getMetaDataMap())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .build();

        apiService.tokenPayment(tokenTransaction)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}