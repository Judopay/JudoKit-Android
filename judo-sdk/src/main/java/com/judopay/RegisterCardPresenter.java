package com.judopay;

import com.judopay.model.Card;
import com.judopay.model.RegisterTransaction;

class RegisterCardPresenter extends BasePaymentPresenter {

    public RegisterCardPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        super(paymentFormView, apiService, scheduler);
    }

    protected void performRegisterCard(String judoId, Card card, String consumerRef, boolean threeDSecureEnabled) {
        this.loading = true;

        paymentFormView.showLoading();

        RegisterTransaction.Builder builder = new RegisterTransaction.Builder()
                .setJudoId(Long.valueOf(judoId))
                .setCardAddress(card.getCardAddress())
                .setCardNumber(card.getCardNumber())
                .setCv2(card.getCv2())
                .setExpiryDate(card.getExpiryDate());

        if (consumerRef != null) {
            builder.setYourConsumerReference(consumerRef);
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