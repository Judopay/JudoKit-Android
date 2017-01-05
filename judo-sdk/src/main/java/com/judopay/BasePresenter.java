package com.judopay;

import com.google.gson.Gson;
import com.judopay.cardverification.AuthorizationListener;
import com.judopay.model.CardVerificationResult;
import com.judopay.model.Receipt;

import java.io.Reader;
import java.util.concurrent.Callable;

import retrofit2.adapter.rxjava.HttpException;
import rx.Single;
import rx.functions.Action1;

abstract class BasePresenter implements AuthorizationListener {

    final JudoApiService apiService;
    final TransactionCallbacks transactionCallbacks;

    private final Gson gson;
    final DeviceDna deviceDna;

    boolean loading;

    BasePresenter(TransactionCallbacks transactionCallbacks, JudoApiService apiService, DeviceDna deviceDna) {
        this.transactionCallbacks = transactionCallbacks;
        this.apiService = apiService;
        this.deviceDna = deviceDna;
        this.gson = new Gson();
    }

    public Action1<Receipt> callback() {
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

    void reconnect() {
        if (loading) {
            transactionCallbacks.showLoading();
        } else {
            transactionCallbacks.hideLoading();
        }
    }

    @Override
    public Single<Receipt> onAuthorizationCompleted(final CardVerificationResult cardVerificationResult, final String receiptId) {
        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return apiService.complete3dSecure(receiptId, cardVerificationResult)
                        .doOnSuccess(on3dSecureCompleted())
                        .doOnError(error());
            }
        });
    }

    private Action1<Receipt> on3dSecureCompleted() {
        return new Action1<Receipt>() {
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
        };
    }

}