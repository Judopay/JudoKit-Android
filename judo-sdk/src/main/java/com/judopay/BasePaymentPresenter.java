package com.judopay;

import com.judopay.payment.Receipt;
import com.judopay.payment.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import rx.functions.Action1;

abstract class BasePaymentPresenter implements ThreeDSecureListener {

    protected final PaymentFormView paymentFormView;
    protected final Scheduler scheduler;
    protected final JudoApiService apiService;

    protected boolean paymentInProgress;

    public BasePaymentPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
    }

    protected Action1<Receipt> callback(final boolean threeDSecureEnabled) {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                if (receipt.isSuccess()) {
                    paymentInProgress = false;
                    paymentFormView.finish(receipt);
                    paymentFormView.hideLoading();
                } else {
                    if (threeDSecureEnabled && receipt.is3dSecureRequired()) {
                        paymentFormView.setLoadingText(R.string.redirecting);
                        paymentFormView.start3dSecureWebView(receipt, BasePaymentPresenter.this);
                    } else {
                        paymentFormView.showDeclinedMessage(receipt);
                    }
                }
            }
        };
    }

    protected Action1<Throwable> error() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                paymentFormView.hideLoading();
                paymentFormView.handleError();
            }
        };
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
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) { }

}