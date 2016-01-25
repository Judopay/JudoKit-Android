package com.judopay.model;

import java.util.Arrays;
import java.util.List;

/**
 * Represents all the currencies that can be used when performing transactions with the judo API.
 */
public class Currency {

    public static final String AUD = "AUD";
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
    public static final String USD = "USD";
    public static final String ZAR = "ZAR";

    public static final String AUSTRALIAN_DOLLAR = "Australian Dollar";
    public static final String CANADIAN_DOLLAR = "Canadian Dollar";
    public static final String SWISS_FRANC = "Swiss Franc";
    public static final String CZECH_REPUBLIC_KRONA = "Czech Republic Krona";
    public static final String DANISH_KRONE = "Danish Krone";
    public static final String EUROS = "Euros";
    public static final String POUNDS_STERLING = "Pounds Sterling";
    public static final String HONG_KONG_DOLLAR = "Hong Kong Dollar";
    public static final String HUNGARIAN_FORINT = "Hungarian Forint";
    public static final String JAPANESE_YEN = "Japanese Yen";
    public static final String NORWEGIAN_KRONE = "Norwegian Krone";
    public static final String NEW_ZEALAND_DOLLAR = "New Zealand Dollar";
    public static final String POLISH_XLOTY = "Polish Xloty";
    public static final String SWEDISH_KRONA = "Swedish Krona";
    public static final String UNITED_STATES_DOLLAR = "United States Dollar";
    public static final String SOUTH_AFRICAN_RAND = "South African Rand";

    public static List<String> currencyCodes() {
        String[] codes = new String[]{AUD, CAD, CHF, CZK, DKK, EUR, GBP, HKD, HUF, JPY, NOK, NZD, PLN, SEK, USD, ZAR};
        return Arrays.asList(codes);
    }

    public static List<String> currencyNames() {
        String[] names = new String[]{
                AUSTRALIAN_DOLLAR,
                CANADIAN_DOLLAR,
                SWISS_FRANC,
                CZECH_REPUBLIC_KRONA,
                DANISH_KRONE,
                EUROS,
                POUNDS_STERLING,
                HONG_KONG_DOLLAR,
                HUNGARIAN_FORINT,
                JAPANESE_YEN,
                NORWEGIAN_KRONE,
                NEW_ZEALAND_DOLLAR,
                POLISH_XLOTY,
                SWEDISH_KRONA,
                UNITED_STATES_DOLLAR,
                SOUTH_AFRICAN_RAND
        };
        return Arrays.asList(names);
    }

}