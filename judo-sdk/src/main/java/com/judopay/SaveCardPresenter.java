package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.SaveCardRequest;

import java.util.Map;

import rx.Single;
import rx.functions.Func1;

import static com.judopay.arch.TextUtil.isEmpty;

class SaveCardPresenter extends BasePresenter {

    SaveCardPresenter(TransactionCallbacks callbacks, JudoApiService apiService, DeviceDna deviceDna, Logger logger) {
        super(callbacks, apiService, deviceDna, logger);
    }

    Single<Receipt> performSaveCard(Card card, Judo judo, final Map<String, Object> userSignals) {
        this.loading = true;
        transactionCallbacks.showLoading();

        final SaveCardRequest.Builder builder = new SaveCardRequest.Builder()
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

        return Single.defer(() -> deviceDna.send(getJsonElements(userSignals))
                .flatMap((Func1<String, Single<Receipt>>) s -> apiService.saveCard(builder.build())));
    }
}
