package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import io.reactivex.Single;

import static com.judopay.arch.TextUtil.isEmpty;

class PaymentPresenter extends BasePresenter {

    PaymentPresenter(TransactionCallbacks callbacks, JudoApiService judoApiService, Logger logger) {
        super(callbacks, judoApiService, logger);
    }

    Single<Receipt> performPayment(Card card, Judo judo) {
        loading = true;
        transactionCallbacks.showLoading();

        return apiService.payment(buildPayment(card, judo));
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

    Single<Receipt> performTokenPayment(final Card card, Judo judo) {
        loading = true;
        transactionCallbacks.showLoading();

        return apiService.tokenPayment(buildTokenPayment(card, judo));
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
