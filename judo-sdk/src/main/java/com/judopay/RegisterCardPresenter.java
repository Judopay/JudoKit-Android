package com.judopay;

import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;

import java.util.concurrent.Callable;

import rx.Single;

import static com.judopay.arch.TextUtil.isEmpty;

class RegisterCardPresenter extends BasePresenter {

    RegisterCardPresenter(TransactionCallbacks callbacks, JudoApiService apiService, DeviceDna deviceDna) {
        super(callbacks, apiService, deviceDna);
    }

    Single<Receipt> performRegisterCard(Card card, Judo judo) {
        this.loading = true;
        transactionCallbacks.showLoading();

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

        if (judo.getAmount() != null) {
            builder.setAmount(judo.getAmount());
        }

        if (card.getAddress() != null) {
            builder.setCardAddress(card.getAddress());
        } else {
            builder.setCardAddress(judo.getAddress());
        }

        return Single.defer(new Callable<Single<Receipt>>() {
            @Override
            public Single<Receipt> call() throws Exception {
                return apiService.registerCard(builder.build());
            }
        });
    }
}