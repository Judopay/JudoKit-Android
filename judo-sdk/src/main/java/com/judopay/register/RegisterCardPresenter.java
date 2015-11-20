package com.judopay.register;

import com.judopay.Client;
import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.PaymentFormView;
import com.judopay.R;
import com.judopay.Scheduler;
import com.judopay.customer.Card;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.Receipt;
import com.judopay.payment.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import rx.functions.Action1;

class RegisterCardPresenter implements PaymentFormListener, ThreeDSecureListener {

    private final PaymentFormView paymentFormView;
    private final JudoApiService apiService;
    private final Scheduler scheduler;

    private boolean paymentInProgress;

    public RegisterCardPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
    }

    @Override
    public void onSubmit(Card card, Consumer consumer, final boolean threeDSecureEnabled) {
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
                .subscribe(new Action1<Receipt>() {
                    @Override
                    public void call(Receipt receipt) {
                        if (receipt.isSuccess()) {
                            paymentInProgress = false;
                            paymentFormView.finish(receipt);
                            paymentFormView.hideLoading();
                        } else {
                            if (threeDSecureEnabled && receipt.is3dSecureRequired()) {
                                paymentFormView.setLoadingText(R.string.redirecting);
                                paymentFormView.start3dSecureWebView(receipt);
                            } else {
                                paymentFormView.showDeclinedMessage(receipt);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        paymentFormView.hideLoading();
                    }
                });

        paymentFormView.showLoading();
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
                .subscribe(new Action1<Receipt>() {
                    @Override
                    public void call(Receipt receipt) {
                        if (receipt.isSuccess()) {
                            paymentInProgress = false;
                            paymentFormView.finish(receipt);
                            paymentFormView.hideLoading();
                        } else {
                            paymentFormView.showDeclinedMessage(receipt);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        paymentFormView.hideLoading();
                    }
                });
    }

    @Override
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) {
    }

}