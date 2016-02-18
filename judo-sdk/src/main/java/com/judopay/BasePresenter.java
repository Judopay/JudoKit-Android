package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Receipt;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import java.io.Reader;

import rx.functions.Action1;

abstract class BasePresenter implements ThreeDSecureListener {

    final JudoApiService apiService;
    final PaymentFormView paymentFormView;
    final Scheduler scheduler;
    private final Gson gson;

    boolean loading;

    BasePresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler, Gson gson) {
        this.paymentFormView = paymentFormView;
        this.apiService = apiService;
        this.scheduler = scheduler;
        this.gson = gson;
    }

    Action1<Receipt> callback() {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                paymentFormView.hideLoading();
                loading = false;

                if (receipt.isSuccess()) {
                    paymentFormView.dismiss3dSecureDialog();
                    paymentFormView.finish(receipt);
                } else {
                    if (receipt.is3dSecureRequired()) {
                        paymentFormView.setLoadingText(R.string.redirecting);
                        paymentFormView.start3dSecureWebView(receipt, BasePresenter.this);
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
                loading = false;
                if (throwable instanceof retrofit2.HttpException) {
                    retrofit2.Response<?> response = ((retrofit2.HttpException) throwable).response();
                    if (response.errorBody() != null) {
                        Reader reader = response.errorBody().charStream();
                        Receipt receipt = gson.fromJson(reader, Receipt.class);
                        paymentFormView.showDeclinedMessage(receipt);
                    }
                } else if (throwable instanceof java.net.UnknownHostException) {
                    paymentFormView.showConnectionErrorDialog();
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
        apiService.complete3dSecure(receiptId, threeDSecureInfo)
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
                }, error());
    }

}