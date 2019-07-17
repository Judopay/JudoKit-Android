package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;

import io.reactivex.Single;

import static com.judopay.arch.TextUtil.isEmpty;

class RegisterCardPresenter extends JudoPresenter {

    RegisterCardPresenter(TransactionCallbacks callbacks, JudoApiService apiService, Logger logger) {
        super(callbacks, apiService, logger);
    }

    Single<Receipt> performRegisterCard(Card card, Judo judo) {
        loading = true;
        getView().showLoading();

        final RegisterCardRequest.Builder builder = new RegisterCardRequest.Builder()
                .setJudoId(judo.getJudoId())
                .setCardNumber(card.getCardNumber())
                .setCv2(card.getSecurityCode())
                .setExpiryDate(card.getExpiryDate())
                .setMetaData(judo.getMetaDataMap())
                .setEmailAddress(judo.getEmailAddress())
                .setMobileNumber(judo.getMobileNumber())
                .setConsumerReference(judo.getConsumerReference());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        if (!isEmpty(judo.getPaymentReference())) {
            builder.setPaymentReference(judo.getPaymentReference());
        }

        if (judo.getCurrency() != null) {
            builder.setCurrency(judo.getCurrency());
        }

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        return apiService.registerCard(builder.build());
    }
}
