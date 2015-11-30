package com.judopay;

import android.os.Bundle;

import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Client;
import com.judopay.model.Consumer;
import com.judopay.model.Location;
import com.judopay.model.TokenTransaction;

import static com.judopay.BundleUtil.toMap;

class TokenPaymentPresenter extends BasePaymentPresenter {

    public TokenPaymentPresenter(PaymentFormView view, JudoApiService judoApiService, AndroidScheduler androidScheduler) {
        super(view, judoApiService, androidScheduler);
    }

    public void performTokenPayment(Card card, CardToken cardToken, Consumer consumer, String judoId, String amount, String currency, String paymentRef, Bundle metaData, boolean threeDSecureEnabled) {
        this.paymentInProgress = true;
        paymentFormView.showLoading();

        TokenTransaction tokenTransaction = new TokenTransaction.Builder()
                .setAmount(amount)
                .setCardAddress(card.getCardAddress())
                .setConsumerLocation(new Location())
                .setCurrency(currency)
                .setJudoId(Long.valueOf(judoId))
                .setYourConsumerReference(consumer.getYourConsumerReference())
                .setYourPaymentReference(paymentRef)
                .setCv2(card.getCv2())
                .setMetaData(toMap(metaData))
                .setEndDate(cardToken.getEndDate())
                .setLastFour(cardToken.getLastFour())
                .setToken(cardToken.getToken())
                .setType(cardToken.getType())
                .build();

        apiService.tokenPayment(tokenTransaction)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(threeDSecureEnabled), error());
    }

}