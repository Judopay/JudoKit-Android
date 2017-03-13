package com.judopay;

import android.support.annotation.Nullable;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import java.util.Map;
import java.util.concurrent.Callable;

import rx.Single;
import rx.functions.Func1;

import static com.judopay.arch.TextUtil.isEmpty;

class PaymentPresenter extends BasePresenter {

    PaymentPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, DeviceDna deviceDna, Logger logger) {
        super(callbacks, judoApiService, deviceDna, logger);
    }

    Single<Receipt> performPayment(Card card, Judo judo, final Map<String, Object> signals) {
        loading = true;
        transactionCallbacks.showLoading();

        final PaymentRequest paymentRequest = buildPayment(card, judo);

        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return deviceDna.send(getJsonElements(signals))
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
                .setConsumerReference(judo.getConsumerReference())
                .setExpiryDate(card.getExpiryDate())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        if (!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
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
                return deviceDna.send(getJsonElements(signals))
                        .flatMap(new Func1<String, Single<Receipt>>() {
                            @Override
                            public Single<Receipt> call(String s) {
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
                .setConsumerReference(judo.getConsumerReference())
                .setCv2(card.getSecurityCode())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap())
                .setToken(judo.getCardToken());

        if (!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        return builder.build();
    }
}