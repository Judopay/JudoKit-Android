package com.judopay;

import com.judopay.customer.Card;
import com.judopay.payment.PaymentFormListener;
import com.judopay.payment.Receipt;
import com.judopay.payment.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import rx.Observable;
import rx.functions.Action1;

abstract class BasePaymentPresenter implements PaymentFormListener, ThreeDSecureListener {

    private final PaymentFormView paymentFormView;
    private final Scheduler scheduler;
    protected final JudoApiService apiService;

    private boolean paymentInProgress;

    public BasePaymentPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
    }

    @Override
    public void onSubmit(Card card, Consumer consumer, final boolean threeDSecureEnabled) {
        this.paymentInProgress = true;

        performApiCall(card, consumer)
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
                            paymentFormView.hideLoading();
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
                        } else {
                            paymentFormView.showDeclinedMessage(receipt);
                        }
                        paymentFormView.hideLoading();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        paymentFormView.hideLoading();
                    }
                });
    }

    @Override
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) { }

}