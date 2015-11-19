package com.judopay.payment;

import com.judopay.Client;
import com.judopay.customer.Card;
import com.judopay.customer.CardToken;
import com.judopay.customer.Location;

public class TokenPaymentFragment extends BasePaymentFragment {

    @Override
    public void onSubmit(Card card) {
        TokenPayment tokenPayment = getArguments().getParcelable(KEY_TOKEN_PAYMENT);

        if (tokenPayment == null) {
            throw new RuntimeException("TokenPayment argument must be provided to TokenPaymentFragment");
        }

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

        performTokenPayment(tokenTransaction);
    }

    private void performTokenPayment(TokenTransaction transaction) {
        onLoadStarted();

        judoApiService.tokenPayment(transaction)
                .subscribe(this);
    }

}