package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Logger;
import com.judopay.cardverification.AuthorizationListener;
import com.judopay.model.CardVerificationResult;
import com.judopay.model.Receipt;

import java.io.Reader;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

abstract class BasePresenter implements AuthorizationListener {
    protected final JudoApiService apiService;
    protected final TransactionCallbacks transactionCallbacks;
    private final Gson gson;
    private final Logger logger;

    boolean loading;

    BasePresenter(TransactionCallbacks transactionCallbacks, JudoApiService apiService, Logger logger) {
        this.transactionCallbacks = transactionCallbacks;
        this.apiService = apiService;
        this.gson = new Gson();
        this.logger = logger;
    }

    Consumer<Receipt> callback() {
        return receipt -> {
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
        };
    }

    Consumer<Throwable> error() {
        return throwable -> {
            if (logger != null) {
                logger.error("Error calling Judopay API", throwable);
            }

            loading = false;
            handleErrorCallback(throwable);
            transactionCallbacks.dismiss3dSecureDialog();
            transactionCallbacks.hideLoading();
        };
    }

    private void handleErrorCallback(Throwable throwable) {
        if (throwable instanceof HttpException) {
            retrofit2.Response<?> response = ((HttpException) throwable).response();
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                Reader reader = errorBody.charStream();
                Receipt receipt = gson.fromJson(reader, Receipt.class);
                transactionCallbacks.onError(receipt);
            }
        } else if (throwable instanceof java.net.UnknownHostException) {
            transactionCallbacks.onConnectionError();
        }
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
        return Single.defer(() -> apiService.complete3dSecure(receiptId, cardVerificationResult)
                .doOnSuccess(on3dSecureCompleted())
                .doOnError(error()));
    }

    private Consumer<Receipt> on3dSecureCompleted() {
        return receipt -> {
            if (receipt.isSuccess()) {
                loading = false;
                transactionCallbacks.onSuccess(receipt);
            } else {
                transactionCallbacks.onDeclined(receipt);
            }
            transactionCallbacks.dismiss3dSecureDialog();
            transactionCallbacks.hideLoading();
        };
    }
}
