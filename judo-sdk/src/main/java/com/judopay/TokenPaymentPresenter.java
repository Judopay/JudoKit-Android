package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.TokenTransaction;
import com.judopay.payment.form.JudoOptions;

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

        apiService.tokenPayment(tokenTransaction)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}