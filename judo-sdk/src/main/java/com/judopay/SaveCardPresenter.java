package com.judopay;

import com.judopay.arch.Logger;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.SaveCardRequest;

import io.reactivex.Single;

import static com.judopay.arch.TextUtil.isEmpty;

class SaveCardPresenter extends JudoPresenter {

    SaveCardPresenter(final TransactionCallbacks callbacks, final JudoApiService apiService, final Logger logger) {
        super(callbacks, apiService, logger);
    }

    Single<Receipt> performSaveCard(final Card card, final Judo judo) {
        loading = true;
        getView().showLoading();

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

        if (card.getAddress() == null) {
            builder.setCardAddress(judo.getAddress());
        } else {
            builder.setCardAddress(card.getAddress());
        }

        return apiService.saveCard(builder.build());
    }
}
