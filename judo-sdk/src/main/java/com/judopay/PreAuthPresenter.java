package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import io.reactivex.Single;

import static com.judopay.arch.TextUtil.isEmpty;

class PreAuthPresenter extends JudoPresenter {

    PreAuthPresenter(final TransactionCallbacks callbacks, final JudoApiService judoApiService, final Logger logger) {
        super(callbacks, judoApiService, logger);
    }

    Single<Receipt> performPreAuth(final Card card, final Judo judo) {
        this.loading = true;
        getView().showLoading();

        return apiService.preAuth(buildPayment(card, judo));
    }

    private PaymentRequest buildPayment(final Card card, final Judo judo) {
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
                .setExpiryDate(card.getExpiryDate())
                .setPrimaryAccountDetails(judo.getPrimaryAccountDetails());

        if (!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

        if (card.getAddress() == null) {
            builder.setCardAddress(judo.getAddress());
        } else {
            builder.setCardAddress(card.getAddress());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        return builder.build();
    }

    Single<Receipt> performTokenPreAuth(final Card card, final Judo judo) {
        this.loading = true;
        getView().showLoading();

        return apiService.tokenPreAuth(buildTokenRequest(card, judo));
    }

    private TokenRequest buildTokenRequest(final Card card, final Judo judo) {
        TokenRequest.Builder builder = new TokenRequest.Builder()
                .setAmount(judo.getAmount())
                .setCurrency(judo.getCurrency())
                .setJudoId(judo.getJudoId())
                .setConsumerReference(judo.getConsumerReference())
                .setCv2(card.getSecurityCode())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setMetaData(judo.getMetaDataMap())
                .setToken(judo.getCardToken())
                .setPrimaryAccountDetails(judo.getPrimaryAccountDetails());

        if (!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

        if (card.getAddress() == null) {
            builder.setCardAddress(judo.getAddress());
        } else {
            builder.setCardAddress(card.getAddress());
        }

        return builder.build();
    }
}
