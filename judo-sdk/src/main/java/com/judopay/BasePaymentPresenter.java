package com.judopay;


import com.google.gson.Gson;
import com.judopay.model.Receipt;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import java.io.IOException;
import java.io.Reader;

import retrofit.HttpException;
import retrofit.Response;
import rx.functions.Action1;

abstract class BasePaymentPresenter implements ThreeDSecureListener {

    final JudoApiService apiService;
    final PaymentFormView paymentFormView;
    final Scheduler scheduler;
    private final Gson gson;

    boolean loading;

    BasePaymentPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler, Gson gson) {
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
        this.gson = gson;
    }

    Action1<Receipt> callback(final boolean threeDSecureEnabled) {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                paymentFormView.hideLoading();
                loading = false;

                if (receipt.isSuccess()) {
                    paymentFormView.dismiss3dSecureDialog();
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

    Action1<Throwable> error() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (throwable instanceof HttpException) {
                    Response<?> response = ((HttpException) throwable).response();
                    if (response.errorBody() != null) {
                        try {
                            Reader reader = response.errorBody().charStream();
                            Receipt receipt = gson.fromJson(reader, Receipt.class);
                            paymentFormView.handleError(receipt);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                paymentFormView.dismiss3dSecureDialog();
                paymentFormView.hideLoading();
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
                        paymentFormView.dismiss3dSecureDialog();
                        paymentFormView.hideLoading();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        paymentFormView.dismiss3dSecureDialog();
                        paymentFormView.hideLoading();
                    }
                });
    }

}