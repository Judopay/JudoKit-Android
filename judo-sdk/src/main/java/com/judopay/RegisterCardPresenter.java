package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.RegisterCardRequest;

class RegisterCardPresenter extends BasePresenter {

    public RegisterCardPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler, Gson gson) {
        super(paymentFormView, apiService, scheduler, gson);
    }

    void performRegisterCard(Card card, JudoOptions options) {
        this.loading = true;

        paymentFormView.showLoading();

        RegisterCardRequest.Builder builder = new RegisterCardRequest.Builder()
                .setJudoId(options.getJudoId())
                .setCardAddress(card.getCardAddress())
                .setCardNumber(card.getCardNumber())
                .setCv2(card.getCv2())
                .setExpiryDate(card.getExpiryDate())
                .setMetaData(options.getMetaDataMap())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .setYourConsumerReference(options.getConsumerRef());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.registerCard(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}