package com.judopay;

import com.judopay.customer.Card;
import com.judopay.customer.CardToken;
import com.judopay.customer.Location;
import com.judopay.payment.Receipt;
import com.judopay.payment.TokenTransaction;

import rx.Observable;

class TokenPaymentPresenter extends BasePaymentPresenter {

    private final TokenPayment tokenPayment;

    public TokenPaymentPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler, TokenPayment tokenPayment) {
        super(paymentFormView, apiService, scheduler);
        this.tokenPayment = tokenPayment;
    }

    @Override
    protected Observable<Receipt> performApiCall(Card card, Consumer consumer) {
        CardToken cardToken = tokenPayment.getCardToken();

        TokenTransaction tokenTransaction = new TokenTransaction.Builder()
                .setAmount(tokenPayment.getAmount())
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCurrency(tokenPayment.getCurrency())
                .setJudoId(Long.valueOf(tokenPayment.getJudoId()))
                .setYourConsumerReference(tokenPayment.getConsumer().getYourConsumerReference())
                .setYourPaymentReference(tokenPayment.getPaymentReference())
                .setCv2(card.getCv2())
                .setYourPaymentMetaData(tokenPayment.getYourMetaData())
                .setEndDate(cardToken.getEndDate())
                .setLastFour(cardToken.getLastFour())
                .setToken(cardToken.getToken())
                .setType(cardToken.getType())
                .build();

        return apiService.tokenPayment(tokenTransaction);
    }

}