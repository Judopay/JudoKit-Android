package com.judopay.model;

import static com.judopay.arch.Preconditions.checkNotEmpty;

@SuppressWarnings("unused")
public final class GooglePayWallet {
    private final String cardNetwork; // eg: "VISA" paymentData.getCardInfo().getCardNetwork()
    private final String cardDetails; // eg: "1234" paymentData.getCardInfo().getCardDetails()
    private final String token; // paymentData.getPaymentMethodToken().getToken()

    private GooglePayWallet(final String cardNetwork, final String cardDetails, final String token) {
        this.cardNetwork = cardNetwork;
        this.cardDetails = cardDetails;
        this.token = token;
    }

    public String getCardNetwork() {
        return cardNetwork;
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public String getToken() {
        return token;
    }

    public static class Builder {
        private String cardNetwork;
        private String cardDetails;
        private String token;

        /**
         * @param cardNetwork the type of the payment method used (i.e. VISA, MASTERCARD, AMEX).
         *                    This can be obtained from by calling
         *                    paymentData.getCardInfo().getCardNetwork()
         * @return The Builder for creating the {@link GooglePayWallet} instance
         */
        public Builder setCardNetwork(final String cardNetwork) {
            this.cardNetwork = cardNetwork;
            return this;
        }

        /**
         * @param cardDetails the details of the payment method used, typically this is the
         *                    last four digits of a customer's card. This can be obtained by calling
         *                    paymentData.getCardInfo().getCardDetails()
         * @return The Builder for creating the {@link GooglePayWallet} instance
         */
        public Builder setCardDetails(final String cardDetails) {
            this.cardDetails = cardDetails;
            return this;
        }

        /**
         * Sets the token received from Google Pay
         *
         * @param token the token as Json string received from Google Pay         *
         * @return The Builder for creating the {@link GooglePayWallet} instance
         */
        public Builder setToken(final String token) {
            this.token = token;
            return this;
        }

        public GooglePayWallet build() {
            checkNotEmpty(cardNetwork);
            checkNotEmpty(cardDetails);
            checkNotEmpty(token);

            return new GooglePayWallet(cardNetwork, cardDetails, token);
        }
    }
}
