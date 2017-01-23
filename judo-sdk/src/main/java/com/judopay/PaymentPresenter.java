package com.judopay;

import android.support.annotation.Nullable;

import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import java.util.Map;
import java.util.concurrent.Callable;

import rx.Single;
import rx.functions.Func1;

class PaymentPresenter extends BasePresenter {

    PaymentPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, DeviceDna deviceDna) {
        super(callbacks, judoApiService, deviceDna);
    }

    Single<Receipt> performPayment(Card card, Judo judo, @Nullable final Map<String, Object> signals) {
        loading = true;
        transactionCallbacks.showLoading();

        final PaymentRequest paymentRequest = buildPayment(card, judo);

        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return deviceDna.send(signals)
                        .flatMap(new Func1<String, Single<Receipt>>() {
                            @Override
                            public Single<Receipt> call(String deviceId) {
                                return apiService.payment(paymentRequest);
                            }
                        });
            }
        });
    }

    private PaymentRequest buildPayment(Card card, Judo judo) {
        PaymentRequest.Builder builder = new PaymentRequest.Builder()
                .setAmount(judo.getAmount())
                .setCardNumber(card.getCardNumber())
                .setCurrency(judo.getCurrency())
                .setCv2(card.getSecurityCode())
                .setJudoId(judo.getJudoId())
                .setYourConsumerReference(judo.getConsumerReference())
                .setExpiryDate(card.getExpiryDate())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap());

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }
        return builder.build();
    }

    Single<Receipt> performTokenPayment(final Card card, Judo judo, @Nullable final Map<String, Object> signals) {
        this.loading = true;
        transactionCallbacks.showLoading();

        final TokenRequest request = buildTokenPayment(card, judo);

        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return deviceDna.send(signals)
                        .flatMap(new Func1<String, Single<Receipt>>() {
                            @Override
                            public Single<Receipt> call(String deviceId) {
                                return apiService.tokenPayment(request);
                            }
                        });
            }
        });
    }

    private TokenRequest buildTokenPayment(Card card, Judo judo) {
        TokenRequest.Builder builder = new TokenRequest.Builder()
                .setAmount(judo.getAmount())
                .setCurrency(judo.getCurrency())
                .setJudoId(judo.getJudoId())
                .setYourConsumerReference(judo.getConsumerReference())
                .setCv2(card.getSecurityCode())
                .setToken(judo.getCardToken())
                .setMetaData(judo.getMetaDataMap())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber());

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        return builder.build();
    }
}