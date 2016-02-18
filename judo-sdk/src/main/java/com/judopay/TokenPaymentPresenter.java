package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.TokenTransaction;

class TokenPaymentPresenter extends BasePresenter {

    public TokenPaymentPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(view, judoApiService, scheduler, gson);
    }

    public void performTokenPayment(Card card, JudoOptions options) {
        this.loading = true;

        paymentFormView.showLoading();

        TokenTransaction tokenTransaction = new TokenTransaction.Builder()
                .setAmount(options.getAmount())
                .setCardAddress(card.getCardAddress())
                .setCurrency(options.getCurrency())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setCv2(card.getCv2())
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