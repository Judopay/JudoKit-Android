package com.judopay;

import com.judopay.customer.Card;
import com.judopay.register.RegisterTransaction;

class RegisterCardPresenter extends BasePaymentPresenter {

    public RegisterCardPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        super(paymentFormView, apiService, scheduler);
    }

    protected void performRegisterCard(Card card, Consumer consumer, boolean threeDSecureEnabled) {
        this.paymentInProgress = true;

        paymentFormView.showLoading();

        RegisterTransaction.Builder builder = new RegisterTransaction.Builder()
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
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