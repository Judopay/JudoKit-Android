package com.judopay;

import android.util.Log;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Receipt;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.secure3d.ThreeDSecureListener;

import java.io.Reader;

import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;

abstract class BasePresenter implements ThreeDSecureListener {

    final JudoApiService apiService;
    final TransactionCallbacks transactionCallbacks;
    final Scheduler scheduler;
    private final Gson gson;

    boolean loading;

    BasePresenter(TransactionCallbacks transactionCallbacks, JudoApiService apiService, Scheduler scheduler, Gson gson) {
        this.transactionCallbacks = transactionCallbacks;
        this.apiService = apiService;
        this.scheduler = scheduler;
        this.gson = gson;
    }

    Action1<Receipt> callback() {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                transactionCallbacks.hideLoading();
                loading = false;

                if (receipt.isSuccess()) {
                    transactionCallbacks.dismiss3dSecureDialog();
                    transactionCallbacks.onSuccess(receipt);
                } else {
                    if (receipt.is3dSecureRequired()) {
                        transactionCallbacks.setLoadingText(R.string.redirecting);
                        transactionCallbacks.start3dSecureWebView(receipt, BasePresenter.this);
                    } else {
                        transactionCallbacks.onDeclined(receipt);
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
                Log.e("Judo", "An error occurred performing the transaction with judo API", throwable);
                handleErrorCallback(throwable);
                transactionCallbacks.dismiss3dSecureDialog();
                transactionCallbacks.hideLoading();
            }

            private void handleErrorCallback(Throwable throwable) {
                if (throwable instanceof HttpException) {
                    retrofit2.Response<?> response = ((HttpException) throwable).response();
                    if (response.errorBody() != null) {
                        Reader reader = response.errorBody().charStream();
                        Receipt receipt = gson.fromJson(reader, Receipt.class);
                        transactionCallbacks.onError(receipt);
                    }
                } else if (throwable instanceof java.net.UnknownHostException) {
                    transactionCallbacks.onConnectionError();
                }
            }
        };
    }

    public void reconnect() {
        if (loading) {
            transactionCallbacks.showLoading();
        } else {
            transactionCallbacks.hideLoading();
        }
    }

    @Override
    public void onAuthorizationWebPageLoaded() {
        this.transactionCallbacks.show3dSecureWebView();
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
                            transactionCallbacks.onSuccess(receipt);
                        } else {
                            transactionCallbacks.onDeclined(receipt);
                        }
                        transactionCallbacks.dismiss3dSecureDialog();
                        transactionCallbacks.hideLoading();
                    }
                }, error());
    }

}