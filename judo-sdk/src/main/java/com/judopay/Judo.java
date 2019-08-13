package com.judopay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.google.android.gms.wallet.WalletConstants;
import com.judopay.api.JudoApiServiceFactory;
import com.judopay.arch.GooglePaymentUtils;
import com.judopay.error.JudoIdInvalidError;
import com.judopay.model.Address;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.CustomLayout;
import com.judopay.model.PaymentMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.judopay.arch.TextUtil.isEmpty;
import static com.judopay.model.LuhnCheck.isValid;

/**
 * The wrapper for providing data to Activity and Fragments classes in the SDK (e.g. PaymentActivity).
 * <p>
 * Use the {@link Judo.Builder class for constructing} an instance of {@link Judo}.
 * When calling an Activity with an Intent extra or a Fragment using an arguments Bundle,
 * use {@link Judo#JUDO_OPTIONS} as the extra or argument name.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Judo implements Parcelable {

    public static final int RESULT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_DECLINED = 2;
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_TOKEN_EXPIRED = 3;
    public static final int RESULT_CONNECTION_ERROR = 5;
    public static final int RESULT_CARD_SCANNED = 6;
    public static final int JUDO_REQUEST = 100;

    // Constants to define different actions (for use with startActivityForResult(...))
    public static final int PAYMENT_REQUEST = 101;
    public static final int TOKEN_PAYMENT_REQUEST = 102;
    public static final int PRE_AUTH_REQUEST = 201;
    public static final int TOKEN_PRE_AUTH_REQUEST = 202;
    public static final int REGISTER_CARD_REQUEST = 301;
    public static final int SAVE_CARD_REQUEST = 302;
    public static final int CHECK_CARD_REQUEST = 303;
    public static final int CARD_SCANNING_REQUEST = 801;
    public static final int PAYMENT_METHOD = 400;
    public static final int GPAY_REQUEST = 500;
    public static final int GPAY_ERROR_RESULT = 501;

    public static final String JUDO_CARD = "JudoCard";
    public static final String JUDO_OPTIONS = "Judo";
    public static final String JUDO_RECEIPT = "JudoReceipt";
    public static final String GPAY_STATUS = "GPayStatus";
    public static final String GPAY_PREAUTH = "GPayPreAuth";

    public static final int UI_CLIENT_MODE_JUDO_SDK = 1;

    public static final int LIVE = 0;
    public static final int SANDBOX = 1;
    public static final int CUSTOM = 2;

    public static final Parcelable.Creator<Judo> CREATOR = new Parcelable.Creator<Judo>() {
        @Override
        public Judo createFromParcel(final Parcel source) {
            return new Judo(source);
        }

        @Override
        public Judo[] newArray(final int size) {
            return new Judo[size];
        }
    };
    private static final int UI_CLIENT_MODE_CUSTOM_UI = 0;

    private String apiToken;
    private String apiSecret;
    private Integer environment;

    private boolean avsEnabled;
    private boolean amexEnabled;
    private boolean maestroEnabled;
    private boolean sslPinningEnabled;
    private boolean rootedDevicesAllowed;

    private String judoId;
    private String amount;
    private String currency;
    private String consumerReference;
    private String paymentReference;
    private Bundle metaData;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private Address address;
    private CardToken cardToken;
    private String emailAddress;
    private String mobileNumber;
    private CustomLayout customLayout;
    private PendingIntent cardScanningIntent;
    private String customEnvironmentHost;
    private EnumSet<PaymentMethod> paymentMethod;
    private boolean isGPayRequireContactDetails;
    private boolean isGPayRequireBillingDetails;
    private boolean isGPayRequireShippingDetails;
    private List<Integer> supportedGPayCards;
    private int environmentModeGPay;

    private Judo() {
    }

    @SuppressWarnings("unchecked")
    protected Judo(final Parcel judoIn) {
        this.apiToken = judoIn.readString();
        this.apiSecret = judoIn.readString();
        this.environment = (Integer) judoIn.readValue(Integer.class.getClassLoader());
        this.avsEnabled = judoIn.readByte() != 0;
        this.amexEnabled = judoIn.readByte() != 0;
        this.maestroEnabled = judoIn.readByte() != 0;
        this.sslPinningEnabled = judoIn.readByte() != 0;
        this.rootedDevicesAllowed = judoIn.readByte() != 0;
        this.judoId = judoIn.readString();
        this.amount = judoIn.readString();
        this.currency = judoIn.readString();
        this.consumerReference = judoIn.readString();
        this.paymentReference = judoIn.readString();
        this.metaData = judoIn.readBundle(getClass().getClassLoader());
        this.cardNumber = judoIn.readString();
        this.expiryMonth = judoIn.readString();
        this.expiryYear = judoIn.readString();
        this.address = judoIn.readParcelable(Address.class.getClassLoader());
        this.cardToken = judoIn.readParcelable(CardToken.class.getClassLoader());
        this.emailAddress = judoIn.readString();
        this.mobileNumber = judoIn.readString();
        this.customLayout = judoIn.readParcelable(CustomLayout.class.getClassLoader());
        this.cardScanningIntent = judoIn.readParcelable(PendingIntent.class.getClassLoader());
        this.customEnvironmentHost = judoIn.readString();
        this.paymentMethod = (EnumSet<PaymentMethod>) judoIn.readSerializable();
        this.isGPayRequireContactDetails = judoIn.readByte() != 0;
        this.isGPayRequireBillingDetails = judoIn.readByte() != 0;
        this.isGPayRequireShippingDetails = judoIn.readByte() != 0;
        this.supportedGPayCards = new ArrayList<>();
        judoIn.readList(this.supportedGPayCards, Integer.class.getClassLoader());
        this.environmentModeGPay = judoIn.readInt();
    }

    public Builder newBuilder() {
        return new Judo.Builder()
                .setApiToken(apiToken)
                .setApiSecret(apiSecret)
                .setEnvironment(environment)
                .setJudoId(judoId)
                .setCardToken(cardToken)
                .setCardNumber(cardNumber)
                .setExpiryMonth(expiryMonth)
                .setExpiryYear(expiryYear)
                .setAmount(amount)
                .setCurrency(currency)
                .setConsumerReference(consumerReference)
                .setPaymentReference(paymentReference)
                .setMetaData(metaData)
                .setAddress(address)
                .setAvsEnabled(avsEnabled)
                .setAmexEnabled(amexEnabled)
                .setMaestroEnabled(maestroEnabled)
                .setSslPinningEnabled(sslPinningEnabled)
                .setRootedDevicesAllowed(rootedDevicesAllowed)
                .setCustomLayout(customLayout)
                .setCardScanningIntent(cardScanningIntent)
                .setPaymentMethod(paymentMethod)
                .setGPayRequireContactDetails(isGPayRequireContactDetails)
                .setGPayRequireBillingDetails(isGPayRequireBillingDetails)
                .setGPayRequireShippingDetails(isGPayRequireShippingDetails)
                .setSupportedGPayCards(supportedGPayCards)
                .setEnvironmentModeGPay(environmentModeGPay);
    }

    public String getAmount() {
        return amount;
    }

    public String getJudoId() {
        return judoId;
    }

    @Currency.Type
    public String getCurrency() {
        return currency;
    }

    public String getConsumerReference() {
        return consumerReference;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public Bundle getMetaData() {
        return metaData;
    }

    public CardToken getCardToken() {
        return cardToken;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isGPayRequireContactDetails() {
        return isGPayRequireContactDetails;
    }

    public boolean isGPayRequireBillingDetails() {
        return isGPayRequireBillingDetails;
    }

    public boolean isGPayRequireShippingDetails() {
        return isGPayRequireShippingDetails;
    }

    public List<Integer> getSupportedGPayCards() {
        return supportedGPayCards;
    }

    public int getEnvironmentModeGPay(){
        return environmentModeGPay;
    }

    public PendingIntent getCardScanningIntent() {
        return cardScanningIntent;
    }

    public CustomLayout getCustomLayout() {
        return customLayout;
    }

    public Map<String, String> getMetaDataMap() {
        Map<String, String> map = new HashMap<>();

        if (metaData != null) {
            for (String key : metaData.keySet()) {
                String value = metaData.getString(key);
                map.put(key, value);
            }
        }
        return map;
    }

    @Environment
    public int getEnvironment() {
        return environment;
    }

    @SuppressWarnings("SameParameterValue")
    public void setEnvironment(@Environment final int environment) {
        if (environment != CUSTOM) {
            customEnvironmentHost = null;
        }

        this.environment = environment;
    }

    public JudoApiService getApiService(final Context context) {
        return JudoApiServiceFactory.createApiService(context, Judo.UI_CLIENT_MODE_CUSTOM_UI, this);
    }

    @SuppressWarnings("SameParameterValue")
    JudoApiService getApiService(final Context context, @UiClientMode final int uiClientMode) {
        return JudoApiServiceFactory.createApiService(context, uiClientMode, this);
    }

    public void setEnvironmentHost(final String customEnvironmentHost) {
        setEnvironment(CUSTOM);
        this.customEnvironmentHost = customEnvironmentHost;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public boolean isSslPinningEnabled() {
        return sslPinningEnabled && environment != CUSTOM;
    }

    public boolean isAvsEnabled() {
        return avsEnabled;
    }

    public boolean isMaestroEnabled() {
        return maestroEnabled;
    }

    public boolean isAmexEnabled() {
        return amexEnabled;
    }

    public boolean isRootedDevicesAllowed() {
        return rootedDevicesAllowed;
    }

    public EnumSet<PaymentMethod> getPaymentMethod() {
        return paymentMethod;
    }

    public String getApiEnvironmentHost(final Context context) {
        String endPoint;
        switch (environment) {
            case SANDBOX:
                endPoint = context.getString(R.string.api_host_sandbox);
                break;
            case CUSTOM:
                endPoint = customEnvironmentHost;
                break;
            default:
                endPoint = context.getString(R.string.api_host_live);
        }
        return endPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.apiToken);
        dest.writeString(this.apiSecret);
        dest.writeValue(this.environment);
        dest.writeByte(this.avsEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.amexEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.maestroEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.sslPinningEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.rootedDevicesAllowed ? (byte) 1 : (byte) 0);
        dest.writeString(this.judoId);
        dest.writeString(this.amount);
        dest.writeString(this.currency);
        dest.writeString(this.consumerReference);
        dest.writeString(this.paymentReference);
        dest.writeBundle(this.metaData);
        dest.writeString(this.cardNumber);
        dest.writeString(this.expiryMonth);
        dest.writeString(this.expiryYear);
        dest.writeParcelable(this.address, flags);
        dest.writeParcelable(this.cardToken, flags);
        dest.writeString(this.emailAddress);
        dest.writeString(this.mobileNumber);
        dest.writeParcelable(this.customLayout, flags);
        dest.writeParcelable(this.cardScanningIntent, flags);
        dest.writeString(this.customEnvironmentHost);
        dest.writeSerializable(this.paymentMethod);
        dest.writeByte(this.isGPayRequireContactDetails ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGPayRequireBillingDetails ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGPayRequireShippingDetails ? (byte) 1 : (byte) 0);
        dest.writeList(this.supportedGPayCards);
        dest.writeInt(this.environmentModeGPay);
    }

    @IntDef({UI_CLIENT_MODE_CUSTOM_UI, UI_CLIENT_MODE_JUDO_SDK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UiClientMode {
    }

    @IntDef({LIVE, SANDBOX, CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Environment {
    }

    public static class Builder {
        private String apiToken;
        private String apiSecret;
        private String judoId;
        private Integer environment;

        private String deviceId;
        private Address address;
        private CardToken cardToken;
        private String cardNumber;
        private String expiryMonth;
        private String expiryYear;
        private String amount;
        private String currency;
        private String consumerReference;
        private String paymentReference;
        private Bundle metaData;
        private PendingIntent cardScanningIntent;
        private CustomLayout customLayout;

        private boolean avsEnabled;
        private boolean amexEnabled = true;
        private boolean maestroEnabled = true;
        private boolean sslPinningEnabled = true;
        private boolean rootedDevicesAllowed = true;
        private EnumSet<PaymentMethod> paymentMethod;
        private boolean isGPayRequireContactDetails = true;
        private boolean isGPayRequireBillingDetails = true;
        private boolean isGPayRequireShippingDetails = false;
        private List<Integer> supportedGPayCards;
        private int environmentModeGPay = WalletConstants.ENVIRONMENT_TEST;

        public Builder() {
        }

        public Builder(final String apiToken, final String apiSecret) {
            this.apiToken = apiToken;
            this.apiSecret = apiSecret;
        }

        public Builder setApiToken(final String apiToken) {
            this.apiToken = apiToken;
            return this;
        }

        public Builder setApiSecret(final String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder setEnvironment(final Integer environment) {
            this.environment = environment;
            return this;
        }

        public Builder setDeviceId(final String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setAmount(final String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setJudoId(final String judoId) {
            this.judoId = judoId.replaceAll("-", "");
            return this;
        }

        public Builder setCurrency(@Currency.Type final String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setConsumerReference(final String consumerReference) {
            this.consumerReference = consumerReference;
            return this;
        }

        public Builder setPaymentReference(final String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public Builder setMetaData(final Bundle metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder setCardNumber(final String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setExpiryMonth(final String expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }

        public Builder setExpiryYear(final String expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }

        public Builder setAddress(final Address address) {
            this.address = address;
            return this;
        }

        public Builder setCardToken(final CardToken cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public Builder setCustomLayout(final CustomLayout customLayout) {
            this.customLayout = customLayout;
            return this;
        }

        public Builder setCardScanningIntent(final PendingIntent cardScanningIntent) {
            this.cardScanningIntent = cardScanningIntent;
            return this;
        }

        public Builder setAvsEnabled(final boolean avsEnabled) {
            this.avsEnabled = avsEnabled;
            return this;
        }

        public Builder setAmexEnabled(final boolean amexEnabled) {
            this.amexEnabled = amexEnabled;
            return this;
        }

        public Builder setMaestroEnabled(final boolean maestroEnabled) {
            this.maestroEnabled = maestroEnabled;
            return this;
        }

        public Builder setSslPinningEnabled(final boolean sslPinningEnabled) {
            this.sslPinningEnabled = sslPinningEnabled;
            return this;
        }

        public Builder setRootedDevicesAllowed(final boolean rootedDevicesAllowed) {
            this.rootedDevicesAllowed = rootedDevicesAllowed;
            return this;
        }

        public Builder setGPayRequireContactDetails(final boolean isGPayRequireContactDetails) {
            this.isGPayRequireContactDetails = isGPayRequireContactDetails;
            return this;
        }

        public Builder setGPayRequireBillingDetails(final boolean isGPayRequireBillingDetails) {
            this.isGPayRequireBillingDetails = isGPayRequireBillingDetails;
            return this;
        }

        public Builder setGPayRequireShippingDetails(final boolean isGPayRequireShippingDetails) {
            this.isGPayRequireShippingDetails = isGPayRequireShippingDetails;
            return this;
        }

        public Builder setSupportedGPayCards(final List<Integer> supportedGPayCards) {
            this.supportedGPayCards = supportedGPayCards;
            return this;
        }

        public Builder setEnvironmentModeGPay(@GooglePaymentUtils.EnvironmentMode final int environmentModeGPay){
            this.environmentModeGPay = environmentModeGPay;
            return this;
        }

        public Judo build() {
            if (isEmpty(judoId) || !isValid(judoId)) {
                throw new JudoIdInvalidError();
            }

            Judo judo = new Judo();

            judo.apiToken = apiToken;
            judo.apiSecret = apiSecret;
            judo.environment = environment;
            judo.cardToken = cardToken;
            judo.cardNumber = cardNumber;
            judo.expiryMonth = expiryMonth;
            judo.expiryYear = expiryYear;
            judo.amount = amount;
            judo.judoId = judoId;
            judo.currency = currency;
            judo.consumerReference = consumerReference;
            judo.paymentReference = paymentReference;
            judo.metaData = metaData;
            judo.address = address;

            judo.avsEnabled = avsEnabled;
            judo.amexEnabled = amexEnabled;
            judo.maestroEnabled = maestroEnabled;
            judo.sslPinningEnabled = sslPinningEnabled;
            judo.rootedDevicesAllowed = rootedDevicesAllowed;

            judo.customLayout = customLayout;
            judo.cardScanningIntent = cardScanningIntent;
            judo.paymentMethod = paymentMethod;

            judo.isGPayRequireContactDetails = isGPayRequireContactDetails;
            judo.isGPayRequireBillingDetails = isGPayRequireBillingDetails;
            judo.isGPayRequireShippingDetails = isGPayRequireShippingDetails;
            judo.supportedGPayCards = supportedGPayCards;
            judo.environmentModeGPay = environmentModeGPay;

            return judo;
        }

        public Builder setPaymentMethod(final EnumSet<PaymentMethod> paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }
    }
}