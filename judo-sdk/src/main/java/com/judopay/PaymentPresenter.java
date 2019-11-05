package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import io.reactivex.Single;

import static com.judopay.arch.TextUtil.isEmpty;

class PaymentPresenter extends JudoPresenter {

    PaymentPresenter(final TransactionCallbacks callbacks, final JudoApiService judoApiService, final Logger logger) {
        super(callbacks, judoApiService, logger);
    }

    Single<Receipt> performPayment(final Card card, final Judo judo) {
        loading = true;
        getView().showLoading();

        return apiService.payment(buildPayment(card, judo));
    }

    private PaymentRequest buildPayment(final Card card, final Judo judo) {
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
                .setMetaData(judo.getMetaDataMap())
                .setPrimaryAccountDetails(judo.getPrimaryAccountDetails());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

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

    Single<Receipt> performTokenPayment(final Card card, final Judo judo) {
        loading = true;
        getView().showLoading();

        return apiService.tokenPayment(buildTokenPayment(card, judo));
    }

    private TokenRequest buildTokenPayment(final Card card, final Judo judo) {
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
