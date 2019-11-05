package com.judopay.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents all the currencies that can be used when performing transactions with the judo API.
 */
@SuppressWarnings("WeakerAccess")
public final class Currency {

    @StringDef({AED, AUD, BRL, CAD, CHF, CZK, DKK, EUR, GBP, HKD, HUF, JPY, NOK, NZD, PLN, QAR, SAR, SEK, SGD, USD, ZAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type { }

    public static final String AED = "AED";
    public static final String AUD = "AUD";
    public static final String BRL = "BRL";
    public static final String CAD = "CAD";
    public static final String CHF = "CHF";
    public static final String CZK = "CZK";
    public static final String DKK = "DKK";
    public static final String EUR = "EUR";
    public static final String GBP = "GBP";
    public static final String HKD = "HKD";
    public static final String HUF = "HUF";
    public static final String JPY = "JPY";
    public static final String NOK = "NOK";
    public static final String NZD = "NZD";
    public static final String PLN = "PLN";
    public static final String SEK = "SEK";
    public static final String SGD = "SGD";
    public static final String QAR = "QAR";
    public static final String SAR = "SAR";
    public static final String USD = "USD";
    public static final String ZAR = "ZAR";

}