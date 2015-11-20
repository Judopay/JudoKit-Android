package com.judopay;

import com.judopay.customer.Card;
import com.judopay.payment.Receipt;
import com.judopay.register.RegisterTransaction;

import rx.Observable;

class RegisterCardPresenter extends BasePaymentPresenter {

    public RegisterCardPresenter(PaymentFormView paymentFormView, JudoApiService apiService, Scheduler scheduler) {
        super(paymentFormView, apiService, scheduler);
    }

    @Override
    protected Observable<Receipt> performApiCall(Card card, Consumer consumer) {
        RegisterTransaction.Builder builder = new RegisterTransaction.Builder()
                .setCardAddress(card.getCardAddress())
                .setClientDetails(new Client())
                .setCardNumber(card.getCardNumber())
                .setCv2(card.getCv2())
                .setExpiryDate(card.getExpiryDate());

        if (consumer != null) {
            builder.setYourConsumerReference(consumer.getYourConsumerReference());
        }

        if (card.startDateAndIssueNumberRequired()) {
            builder.setIssueNumber(card.getIssueNumber())
                    .setStartDate(card.getStartDate());
        }

        return apiService.registerCard(builder.build());
    }

}