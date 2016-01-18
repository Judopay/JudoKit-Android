package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.PaymentTransaction;
import com.judopay.payment.form.JudoOptions;

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
                .setConsumerLocation(null)
                .setCardNumber(card.getCardNumber())
                .setCurrency(options.getCurrency())
                .setCv2(card.getCv2())
                .setJudoId(options.getJudoId())
                .setYourConsumerReference(options.getConsumerRef())
                .setExpiryDate(card.getExpiryDate());

        if (options.getMetaData() != null) {
            builder.setMetaData(options.getMetaDataMap());
        }

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
