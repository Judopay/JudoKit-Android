package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.PaymentTransaction;

class PreAuthPresenter extends BasePresenter {

    public PreAuthPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(view, judoApiService, scheduler, gson);
    }

    public void performPreAuth(Card card, JudoOptions options) {
        this.loading = true;

        paymentFormView.showLoading();

        PaymentTransaction.Builder builder = new PaymentTransaction.Builder()
                .setAmount(options.getAmount())
                .setCardAddress(card.getCardAddress())
                .setCardNumber(card.getCardNumber())
                .setCurrency(options.getCurrency())
                .setCv2(card.getCv2())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setEmailAddress(options.getEmailAddress())
                .setMobileNumber(options.getMobileNumber())
                .setMetaData(options.getMetaDataMap())
                .setExpiryDate(card.getExpiryDate());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.preAuth(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}
