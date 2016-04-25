package com.judopay.model;

import android.support.annotation.StringRes;

import com.judopay.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A Country that can be selected by the user when providing information for
 * address verification (AVS) checks during a transaction.
 */
public class Country {

    public static final String UNITED_KINGDOM = "UK";
    public static final String UNITED_STATES = "USA";
    public static final String CANADA = "Canada";
    public static final String OTHER = "Other";

    private final int code;
    private final String displayName;

    public Country(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

        public static List<String> avsCountries() {
        ArrayList<String> countries = new ArrayList<>();

        countries.add(Country.UNITED_KINGDOM);
        countries.add(Country.UNITED_STATES);
        countries.add(Country.CANADA);
        countries.add(Country.OTHER);

        return countries;
    }

    public static int codeFromCountry(String country) {
        switch (country) {
            case Country.UNITED_KINGDOM:
                return 826;
            case Country.UNITED_STATES:
                return 840;
            case Country.CANADA:
                return 124;
            default:
                return 0;
        }
    }

    @StringRes
    public static int postcodeName(String country) {
        switch (country) {
            case Country.UNITED_STATES:
                return R.string.billing_zip_code;

            case Country.CANADA:
                return R.string.billing_postal_code;

            default:
                return R.string.billing_postcode;
        }
    }

}