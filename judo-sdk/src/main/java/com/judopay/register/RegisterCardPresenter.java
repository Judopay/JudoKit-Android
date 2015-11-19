package com.judopay.register;

import com.judopay.Client;
import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.customer.Card;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.Receipt;

import rx.Observer;

class RegisterCardPresenter implements PaymentFormListener, Observer<Receipt> {

    private final Consumer consumer;
    private final PaymentFormView paymentFormView;
    private final JudoApiService apiService;

    private boolean paymentInProgress;

    public RegisterCardPresenter(Consumer consumer,
                                 PaymentFormView paymentFormView,
                                 JudoApiService apiService) {
        this.consumer = consumer;
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
    }

    @Override
    public void onSubmit(Card card) {
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

        this.paymentInProgress = true;

        apiService.registerCard(builder.build())
                .subscribe(this);

        paymentFormView.showLoading();
    }

    @Override
    public void onCompleted() {
        paymentFormView.hideLoading();
    }

    @Override
    public void onError(Throwable e) {
        paymentFormView.hideLoading();
    }

    @Override
    public void onNext(Receipt receipt) {
        if (receipt.isSuccess()) {
            paymentFormView.finish(receipt);
        } else {
            paymentFormView.showDeclinedMessage(receipt);
        }
    }

    public void reconnect() {
        if (paymentInProgress) {
            paymentFormView.showLoading();
        } else {
            paymentFormView.hideLoading();
        }
    }
}
