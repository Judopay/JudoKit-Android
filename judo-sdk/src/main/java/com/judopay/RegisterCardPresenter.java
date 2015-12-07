package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.Consumer;
import com.judopay.model.RegisterTransaction;

class RegisterCardPresenter extends BasePaymentPresenter {

    public RegisterCardPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler, Gson gson) {
        super(paymentFormView, apiService, scheduler, gson);
    }

    void performRegisterCard(String judoId, Card card, Consumer consumer, boolean threeDSecureEnabled) {
        this.loading = true;

        paymentFormView.showLoading();

        RegisterTransaction.Builder builder = new RegisterTransaction.Builder()
                .setJudoId(Long.valueOf(judoId))
                .setCardAddress(card.getCardAddress())
                .setCardNumber(card.getCardNumber())
                .setCv2(card.getCv2())
                .setExpiryDate(card.getExpiryDate());

        if (consumer != null) {
            builder.setYourConsumerReference(consumer.getYourConsumerReference());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.registerCard(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(threeDSecureEnabled), error());
    }

}