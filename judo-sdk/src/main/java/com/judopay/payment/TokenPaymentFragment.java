package com.judopay.payment;

import com.judopay.Client;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.customer.CardToken;
import com.judopay.customer.Location;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TokenPaymentFragment extends BasePaymentFragment {

    @Override
    public void onSubmit(Card card) {
        TokenPayment tokenPayment = getArguments().getParcelable(KEY_TOKEN_PAYMENT);

        if (tokenPayment == null) {
            throw new RuntimeException("TokenPayment argument must be provided to TokenPaymentFragment");
        }

        TokenTransaction.Builder builder = new TokenTransaction.Builder()
                .setAmount(tokenPayment.getAmount())
                .setCardAddress(new Address.Builder()
                        .setPostCode(card.getCardAddress().getPostcode())
                        .build())
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

}