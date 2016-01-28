package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.PaymentTransaction;

class PaymentPresenter extends BasePresenter {

    public PaymentPresenter(PaymentFormView view, JudoApiService judoApiService, Scheduler scheduler, Gson gson) {
        super(view, judoApiService, scheduler, gson);
    }

    public void performPayment(Card card, JudoOptions judoOptions) {
        this.loading = true;

        paymentFormView.showLoading();

        PaymentTransaction.Builder builder = new PaymentTransaction.Builder()
                .setAmount(judoOptions.getAmount())
                .setCardAddress(card.getCardAddress())
                .setConsumerLocation(null)
                .setCardNumber(card.getCardNumber())
                .setCurrency(judoOptions.getCurrency())
                .setCv2(card.getCv2())
                .setJudoId(judoOptions.getJudoId())
                .setYourConsumerReference(judoOptions.getConsumerRef())
                .setExpiryDate(card.getExpiryDate());

        if (judoOptions.getMetaData() != null) {
            builder.setMetaData(judoOptions.getMetaDataMap());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        apiService.payment(builder.build())
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(), error());
    }

}
