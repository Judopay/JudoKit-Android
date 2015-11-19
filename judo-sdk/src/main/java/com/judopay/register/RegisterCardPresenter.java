package com.judopay.register;

import com.judopay.Client;
import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.R;
import com.judopay.Scheduler;
import com.judopay.customer.Card;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.Receipt;
import com.judopay.payment.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import rx.Observer;

class RegisterCardPresenter implements PaymentFormListener, Observer<Receipt>, ThreeDSecureListener {

    private final Consumer consumer;
    private final PaymentFormView paymentFormView;
    private final JudoApiService apiService;
    private final boolean threeDSecureEnabled;
    private final Scheduler scheduler;

    private boolean paymentInProgress;

    public RegisterCardPresenter(Consumer consumer,
                                 PaymentFormView paymentFormView,
                                 JudoApiService apiService,
                                 Scheduler scheduler, boolean threeDSecureEnabled) {
        this.consumer = consumer;
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
        this.threeDSecureEnabled = threeDSecureEnabled;
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
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(this);

        paymentFormView.showLoading();
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        paymentFormView.hideLoading();
    }

    @Override
    public void onNext(Receipt receipt) {
        if (receipt.isSuccess()) {
            paymentInProgress = false;
            paymentFormView.finish(receipt);
            paymentFormView.hideLoading();
        } else {
            handle3dSecureOrDeclinedPayment(receipt);
        }
    }

    private void handle3dSecureOrDeclinedPayment(Receipt receipt) {
        if (threeDSecureEnabled && receipt.is3dSecureRequired()) {
            paymentFormView.setLoadingText(R.string.redirecting);
            paymentFormView.start3dSecureWebView(receipt);
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

    @Override
    public void onAuthorizationWebPageLoaded() {
        this.paymentFormView.show3dSecureWebView();
    }

    @Override
    public void onAuthorizationCompleted(ThreeDSecureInfo threeDSecureInfo, String receiptId) {
        apiService.threeDSecurePayment(receiptId, threeDSecureInfo)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(this);
    }

    @Override
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) {
    }

}