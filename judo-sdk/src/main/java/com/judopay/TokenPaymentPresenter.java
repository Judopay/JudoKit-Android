package com.judopay;

import android.os.Bundle;

import com.judopay.customer.Card;
import com.judopay.customer.CardToken;
import com.judopay.customer.Location;
import com.judopay.payment.TokenPayment;

import static com.judopay.BundleUtil.toMap;

class TokenPaymentPresenter extends BasePaymentPresenter {

    public TokenPaymentPresenter(PaymentFormView view, JudoApiService judoApiService, AndroidScheduler androidScheduler) {
        super(view, judoApiService, androidScheduler);
    }

    public void performTokenPayment(Card card, CardToken cardToken, Consumer consumer, String judoId, String amount, String currency, String paymentRef, Bundle metaData, boolean threeDSecureEnabled) {
        this.paymentInProgress = true;
        paymentFormView.showLoading();

        TokenPayment tokenPayment = new TokenPayment.Builder()
                .setAmount(amount)
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
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

        apiService.tokenPayment(tokenPayment)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe(callback(threeDSecureEnabled), error());
    }

}