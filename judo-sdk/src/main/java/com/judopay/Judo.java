package com.judopay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.judopay.api.JudoApiServiceFactory;
import com.judopay.error.JudoIdInvalidError;
import com.judopay.model.Address;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.CustomLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
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
@SuppressWarnings("unused")
public class Judo implements Parcelable {

    @IntDef({UI_CLIENT_MODE_CUSTOM_UI, UI_CLIENT_MODE_JUDO_SDK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UiClientMode {
    }

    @IntDef({LIVE, SANDBOX, CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Environment {
    }

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
    public static final int CARD_SCANNING_REQUEST = 801;

    public static final String JUDO_CARD = "JudoCard";
    public static final String JUDO_OPTIONS = "Judo";
    public static final String JUDO_RECEIPT = "JudoReceipt";

    private static final int UI_CLIENT_MODE_CUSTOM_UI = 0;
    public static final int UI_CLIENT_MODE_JUDO_SDK = 1;

    public static final int LIVE = 0;
    public static final int SANDBOX = 1;
    public static final int CUSTOM = 2;

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
    private String consumerRef;
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

    private Judo() {
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
                .setConsumerRef(consumerRef)
                .setMetaData(metaData)
                .setAddress(address)
                .setEmailAddress(emailAddress)
                .setMobileNumber(mobileNumber)
                .setAvsEnabled(avsEnabled)
                .setAmexEnabled(amexEnabled)
                .setMaestroEnabled(maestroEnabled)
                .setSslPinningEnabled(sslPinningEnabled)
                .setRootedDevicesAllowed(rootedDevicesAllowed)
                .setCustomLayout(customLayout)
                .setCardScanningIntent(cardScanningIntent);
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

    public String getConsumerRef() {
        return consumerRef;
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

    public JudoApiService getApiService(Context context) {
        //noinspection WrongConstant
        return JudoApiServiceFactory.createApiService(context, Judo.UI_CLIENT_MODE_CUSTOM_UI, this);
    }

    @SuppressWarnings("SameParameterValue")
    JudoApiService getApiService(Context context, @UiClientMode int uiClientMode) {
        return JudoApiServiceFactory.createApiService(context, uiClientMode, this);
    }

    @SuppressWarnings("SameParameterValue")
    public void setEnvironment(@Environment int environment) {
        if (environment != CUSTOM)
            customEnvironmentHost = null;

        this.environment = environment;
    }

    public void setEnvironmentHost(String customEnvironmentHost) {
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

    public String getApiEnvironmentHost(Context context) {
        switch (environment) {
            case SANDBOX:
                return context.getString(R.string.api_host_sandbox);
            case CUSTOM:
                return customEnvironmentHost;
            default:
                return context.getString(R.string.api_host_live);
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private String apiToken;
        private String apiSecret;
        private String judoId;
        private Integer environment;

        private Address address;
        private CardToken cardToken;
        private String cardNumber;
        private String expiryMonth;
        private String expiryYear;
        private String amount;
        private String currency;
        private String consumerRef;
        private Bundle metaData;
        private String emailAddress;
        private String mobileNumber;
        private PendingIntent cardScanningIntent;
        private CustomLayout customLayout;

        private boolean avsEnabled;
        private boolean amexEnabled = true;
        private boolean maestroEnabled = true;
        private boolean sslPinningEnabled = true;
        private boolean rootedDevicesAllowed = true;

        public Builder() {
        }

        public Builder(String apiToken, String apiSecret) {
            this.apiToken = apiToken;
            this.apiSecret = apiSecret;
        }

        public Builder setApiToken(String apiToken) {
            this.apiToken = apiToken;
            return this;
        }

        public Builder setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder setEnvironment(Integer environment) {
            this.environment = environment;
            return this;
        }

        public Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setJudoId(String judoId) {
            this.judoId = judoId.replaceAll("-", "");
            return this;
        }

        public Builder setCurrency(@Currency.Type String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setConsumerRef(String consumerRef) {
            this.consumerRef = consumerRef;
            return this;
        }

        public Builder setMetaData(Bundle metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setExpiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }

        public Builder setExpiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }

        public Builder setAddress(Address address) {
            this.address = address;
            return this;
        }

        public Builder setCardToken(CardToken cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder setCustomLayout(CustomLayout customLayout) {
            this.customLayout = customLayout;
            return this;
        }

        public Builder setCardScanningIntent(PendingIntent cardScanningIntent) {
            this.cardScanningIntent = cardScanningIntent;
            return this;
        }

        public Builder setAvsEnabled(boolean avsEnabled) {
            this.avsEnabled = avsEnabled;
            return this;
        }

        public Builder setAmexEnabled(boolean amexEnabled) {
            this.amexEnabled = amexEnabled;
            return this;
        }

        public Builder setMaestroEnabled(boolean maestroEnabled) {
            this.maestroEnabled = maestroEnabled;
            return this;
        }

        public Builder setSslPinningEnabled(boolean sslPinningEnabled) {
            this.sslPinningEnabled = sslPinningEnabled;
            return this;
        }

        public Builder setRootedDevicesAllowed(boolean rootedDevicesAllowed) {
            this.rootedDevicesAllowed = rootedDevicesAllowed;
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
            judo.consumerRef = consumerRef;
            judo.metaData = metaData;
            judo.emailAddress = emailAddress;
            judo.mobileNumber = mobileNumber;
            judo.address = address;

            judo.avsEnabled = avsEnabled;
            judo.amexEnabled = amexEnabled;
            judo.maestroEnabled = maestroEnabled;
            judo.sslPinningEnabled = sslPinningEnabled;
            judo.rootedDevicesAllowed = rootedDevicesAllowed;

            judo.customLayout = customLayout;
            judo.cardScanningIntent = cardScanningIntent;

            return judo;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
        dest.writeString(this.consumerRef);
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
    }

    protected Judo(Parcel in) {
        this.apiToken = in.readString();
        this.apiSecret = in.readString();
        this.environment = (Integer) in.readValue(Integer.class.getClassLoader());
        this.avsEnabled = in.readByte() != 0;
        this.amexEnabled = in.readByte() != 0;
        this.maestroEnabled = in.readByte() != 0;
        this.sslPinningEnabled = in.readByte() != 0;
        this.rootedDevicesAllowed = in.readByte() != 0;
        this.judoId = in.readString();
        this.amount = in.readString();
        this.currency = in.readString();
        this.consumerRef = in.readString();
        this.metaData = in.readBundle();
        this.cardNumber = in.readString();
        this.expiryMonth = in.readString();
        this.expiryYear = in.readString();
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.cardToken = in.readParcelable(CardToken.class.getClassLoader());
        this.emailAddress = in.readString();
        this.mobileNumber = in.readString();
        this.customLayout = in.readParcelable(CustomLayout.class.getClassLoader());
        this.cardScanningIntent = in.readParcelable(PendingIntent.class.getClassLoader());
        this.customEnvironmentHost = in.readString();
    }

    public static final Creator<Judo> CREATOR = new Creator<Judo>() {
        @Override
        public Judo createFromParcel(Parcel source) {
            return new Judo(source);
        }

        @Override
        public Judo[] newArray(int size) {
            return new Judo[size];
        }
    };
}