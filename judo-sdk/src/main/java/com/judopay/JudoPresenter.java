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

abstract class JudoPresenter extends BasePresenter<TransactionCallbacks> implements AuthorizationListener {
    protected final JudoApiService apiService;
    private final Gson gson;
    private final Logger logger;

    boolean loading;

    JudoPresenter(final TransactionCallbacks transactionCallbacks, final JudoApiService apiService, final Logger logger) {
        super(transactionCallbacks);
        this.apiService = apiService;
        this.gson = new Gson();
        this.logger = logger;
    }

    Consumer<Receipt> callback() {
        return receipt -> {
            getView().hideLoading();
            loading = false;

            if (receipt.isSuccess()) {
                getView().dismiss3dSecureDialog();
                getView().onSuccess(receipt);
            } else {
                if (receipt.is3dSecureRequired()) {
                    getView().setLoadingText(R.string.redirecting);
                    getView().start3dSecureWebView(receipt, JudoPresenter.this);
                } else {
                    getView().onDeclined(receipt);
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
            getView().dismiss3dSecureDialog();
            getView().hideLoading();
        };
    }

    private void handleErrorCallback(final Throwable throwable) {
        if (throwable instanceof HttpException) {
            retrofit2.Response<?> response = ((HttpException) throwable).response();
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                Reader reader = errorBody.charStream();
                Receipt receipt = gson.fromJson(reader, Receipt.class);
                getView().onError(receipt);
            }
        } else if (throwable instanceof java.net.UnknownHostException) {
            getView().onConnectionError();
        }
    }

    void reconnect() {
        if (loading) {
            getView().showLoading();
        } else {
            getView().hideLoading();
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
                getView().onSuccess(receipt);
            } else {
                getView().onDeclined(receipt);
            }
            getView().dismiss3dSecureDialog();
            getView().hideLoading();
        };
    }
}
