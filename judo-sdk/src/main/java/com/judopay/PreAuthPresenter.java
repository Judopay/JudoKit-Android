package com.judopay;

import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import java.util.Map;
import java.util.concurrent.Callable;

import rx.Single;
import rx.functions.Func1;

import static com.judopay.arch.TextUtil.isEmpty;

class PreAuthPresenter extends BasePresenter {

    PreAuthPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, DeviceDna deviceDna) {
        super(callbacks, judoApiService, deviceDna);
    }

    Single<Receipt> performPreAuth(Card card, Judo judo, final Map<String, Object> signals) {
        this.loading = true;
        transactionCallbacks.showLoading();

        final PaymentRequest request = buildPayment(card, judo);

        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return deviceDna.send(getJsonElements(signals))
                        .flatMap(new Func1<String, Single<Receipt>>() {
                            @Override
                            public Single<Receipt> call(String deviceId) {
                                return apiService.preAuth(request);
                            }
                        });
            }
        });
    }

    Single<Receipt> performTokenPreAuth(Card card, Judo judo, final Map<String, Object> signals) {
        this.loading = true;
        transactionCallbacks.showLoading();

        final TokenRequest request = buildTokenRequest(card, judo);

        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return deviceDna.send(getJsonElements(signals))
                        .flatMap(new Func1<String, Single<Receipt>>() {
                            @Override
                            public Single<Receipt> call(String deviceId) {
                                return apiService.tokenPreAuth(request);
                            }
                        });
            }
        });
    }

    private TokenRequest buildTokenRequest(Card card, Judo judo) {
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

        if(!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        return builder.build();
    }

    private PaymentRequest buildPayment(Card card, Judo judo) {
        PaymentRequest.Builder builder = new PaymentRequest.Builder()
                .setAmount(judo.getAmount())
                .setCardNumber(card.getCardNumber())
                .setCurrency(judo.getCurrency())
                .setCv2(card.getSecurityCode())
                .setJudoId(judo.getJudoId())
                .setConsumerReference(judo.getConsumerReference())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap())
                .setExpiryDate(card.getExpiryDate());

        if(!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

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

}