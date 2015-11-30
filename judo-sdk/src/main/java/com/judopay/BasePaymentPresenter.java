package com.judopay;

import com.judopay.model.Receipt;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import rx.functions.Action1;

abstract class BasePaymentPresenter implements ThreeDSecureListener {

    protected final Scheduler scheduler;
    protected final JudoApiService apiService;
    protected final PaymentFormView paymentFormView;

    protected boolean loading;

    public BasePaymentPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
    }

    protected Action1<Receipt> callback(final boolean threeDSecureEnabled) {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                paymentFormView.hideLoading();
                loading = false;

                if (receipt.isSuccess()) {
                    paymentFormView.finish(receipt);
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
        if (loading) {
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
                            loading = false;
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