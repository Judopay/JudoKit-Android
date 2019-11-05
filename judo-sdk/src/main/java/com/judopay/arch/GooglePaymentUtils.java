package com.judopay.arch;

import android.content.Context;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.CardInfo;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.judopay.Judo;
import com.judopay.model.GooglePayRequest;
import com.judopay.model.GooglePayWallet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

/**
 * Contains helper static methods for dealing with the Google Payments Api.
 */
public class GooglePaymentUtils {

    private static final List<Integer> DEFAULT_SUPPORTED_CARDS = Arrays.asList(WalletConstants.CARD_NETWORK_AMEX,
            WalletConstants.CARD_NETWORK_MASTERCARD,
            WalletConstants.CARD_NETWORK_VISA,
            WalletConstants.CARD_NETWORK_DISCOVER);

    @IntDef({WalletConstants.ENVIRONMENT_TEST, WalletConstants.ENVIRONMENT_PRODUCTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EnvironmentMode {
    }

    /**
     * Create an instance of {@link PaymentsClient}
     *
     * @param context The context to use.
     * @param environmentMode The environment mode it can be {@link WalletConstants#ENVIRONMENT_TEST} or {@link WalletConstants#ENVIRONMENT_PRODUCTION}
     * @return paymentsClient object
     */
    public static PaymentsClient getGooglePayPaymentsClient(@NonNull final Context context, @EnvironmentMode final int environmentMode) {
        return Wallet.getPaymentsClient(context, new Wallet.WalletOptions.Builder()
                .setEnvironment(environmentMode)
                .build());
    }

    /**
     * Check if payments client is ready for use.
     *
     * @param paymentsClient used to send the request.
     * @param googlePayIsReadyResult The callback listener for the payments client.
     */
    public static void checkIsReadyGooglePay(@NonNull final PaymentsClient paymentsClient, @NonNull final GooglePayIsReadyResult googlePayIsReadyResult) {
        final IsReadyToPayRequest isReadyRequest = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build();

        final Task<Boolean> task = paymentsClient.isReadyToPay(isReadyRequest);
        task.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull final Task<Boolean> task) {
                try {
                    boolean result = task.getResult(ApiException.class);
                    googlePayIsReadyResult.setResult(result);
                } catch (ApiException e) {
                    googlePayIsReadyResult.setResult(false);
                }
            }
        });
    }

    /**
     * Create payment data request based on judo object.
     *
     * @param judo the judo instance
     * @return payment data request object
     */
    public static PaymentDataRequest createDefaultPaymentDataRequest(@NonNull final Judo judo) {

        final CardRequirements cardRequirements = getCardRequirements(judo);

        final PaymentMethodTokenizationParameters methodTokenizationParameters = getPaymentMethodTokenizationParameters(judo.getJudoId());

        return PaymentDataRequest.newBuilder()
                .setTransactionInfo(getTransactionInfo(judo))
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .setCardRequirements(cardRequirements)
                .setPaymentMethodTokenizationParameters(methodTokenizationParameters)
                .setPhoneNumberRequired(judo.isGPayRequireContactDetails())
                .setEmailRequired(judo.isGPayRequireContactDetails())
                .setShippingAddressRequired(judo.isGPayRequireShippingDetails())
                .build();
    }

    private static CardRequirements getCardRequirements(@NonNull final Judo judo) {
        List<Integer> supportedCards;
        if (judo.getSupportedGPayCards() == null || judo.getSupportedGPayCards().isEmpty()) {
            supportedCards = DEFAULT_SUPPORTED_CARDS;
        } else {
            supportedCards = judo.getSupportedGPayCards();
        }

        return CardRequirements.newBuilder()
                .addAllowedCardNetworks(supportedCards)
                .setAllowPrepaidCards(true)
                .setBillingAddressRequired(judo.isGPayRequireBillingDetails())
                .setBillingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL)
                .build();
    }

    private static PaymentMethodTokenizationParameters getPaymentMethodTokenizationParameters(@NonNull final String judoId) {

        return PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", "judopay")
                .addParameter("gatewayMerchantId", judoId)
                .build();
    }

    private static TransactionInfo getTransactionInfo(@NonNull final Judo judo) {
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice(judo.getAmount())
                .setCurrencyCode(judo.getCurrency())
                .build();
    }

    /**
     * Create Google Pay Request object based on {@link Judo} and {@link PaymentData}
     * @param judo the judo instance
     * @param paymentData result of payment
     * @return google pay request for Judopay API using Google Pay
     */
    public static GooglePayRequest createGooglePayRequest(@NonNull final Judo judo, @NonNull final PaymentData paymentData) {
        PaymentMethodToken token = paymentData.getPaymentMethodToken();

        if (token == null) {
            return null;
        } else {
            CardInfo cardInfo = paymentData.getCardInfo();

            GooglePayWallet googlePayWallet = new GooglePayWallet.Builder()
                    .setToken(token.getToken())
                    .setCardNetwork(cardInfo.getCardNetwork())
                    .setCardDetails(cardInfo.getCardDetails())
                    .build();

            return new GooglePayRequest.Builder()
                    .setGooglePayWallet(googlePayWallet)
                    .setAmount(judo.getAmount())
                    .setCurrency(judo.getCurrency())
                    .setJudoId(judo.getJudoId())
                    .setConsumerReference(judo.getConsumerReference())
                    .setPrimaryAccountDetails(judo.getPrimaryAccountDetails())
                    .build();
        }
    }
}
