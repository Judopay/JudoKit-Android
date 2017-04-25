package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;

import java.util.Map;
import java.util.concurrent.Callable;

import rx.Single;
import rx.functions.Func1;

import static com.judopay.arch.TextUtil.isEmpty;

class RegisterCardPresenter extends BasePresenter {

    RegisterCardPresenter(TransactionCallbacks callbacks, JudoApiService apiService, DeviceDna deviceDna, Logger logger) {
        super(callbacks, apiService, deviceDna, logger);
    }

    Single<Receipt> performRegisterCard(Card card, Judo judo, final Map<String, Object> userSignals) {
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
                return deviceDna.send(getJsonElements(userSignals))
                        .flatMap(new Func1<String, Single<Receipt>>() {
                            @Override
                            public Single<Receipt> call(String s) {
                                return apiService.registerCard(builder.build());
                            }
                        });
            }
        });
    }
}