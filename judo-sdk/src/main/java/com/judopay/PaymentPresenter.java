package com.judopay;

import com.judopay.customer.Card;
import com.judopay.customer.Location;
import com.judopay.payment.PaymentTransaction;
import com.judopay.payment.Receipt;

import rx.Observable;

class PaymentPresenter extends BasePaymentPresenter {

    private final Payment payment;

    public PaymentPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler, Payment payment) {
        super(paymentFormView, apiService, scheduler);
        this.payment = payment;
    }

    @Override
    protected Observable<Receipt> performApiCall(Card card, Consumer consumer) {
        PaymentTransaction.Builder builder = new PaymentTransaction.Builder()
                .setAmount(payment.getAmount())
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
                .setConsumerLocation(new Location())
                .setCardNumber(card.getCardNumber())
                .setCurrency(payment.getCurrency())
                .setCv2(card.getCv2())
                .setJudoId(Long.valueOf(payment.getJudoId()))
                .setYourConsumerReference(payment.getConsumer().getYourConsumerReference())
                .setYourPaymentReference(payment.getPaymentRef())
                .setExpiryDate(card.getExpiryDate());

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        return apiService.payment(builder.build());
    }

}