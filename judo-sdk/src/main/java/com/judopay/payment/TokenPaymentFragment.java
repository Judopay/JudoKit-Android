package com.judopay.payment;

import com.judopay.Client;
import com.judopay.customer.Card;
import com.judopay.customer.CardToken;
import com.judopay.customer.Location;

public class TokenPaymentFragment extends BasePaymentFragment {

    @Override
    public void onSubmit(Card card) {
        TokenPayment tokenPayment = getArguments().getParcelable(KEY_TOKEN_PAYMENT);

        TokenTransaction.Builder builder = new TokenTransaction.Builder();

        builder.setAmount(tokenPayment.getAmount())
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCurrency(tokenPayment.getCurrency())
                .setJudoId(Long.valueOf(tokenPayment.getJudoId()))
                .setYourConsumerReference(tokenPayment.getConsumer().getYourConsumerReference())
                .setYourPaymentReference(tokenPayment.getPaymentReference())
                .setCv2(card.getCv2())
                .setYourPaymentMetaData(tokenPayment.getYourMetaData());

        CardToken cardToken = tokenPayment.getCardToken();

        builder.setEndDate(cardToken.getEndDate())
                .setLastFour(cardToken.getLastFour())
                .setToken(cardToken.getToken())
                .setType(cardToken.getType());

        performTokenPayment(builder.build());
    }

    private void performTokenPayment(TokenTransaction transaction) {
        onLoadStarted();
        paymentApiService.tokenPayment(transaction)
                .subscribe(this);
    }

}