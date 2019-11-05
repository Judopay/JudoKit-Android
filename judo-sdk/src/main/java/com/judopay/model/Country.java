package com.judopay.model;

import androidx.annotation.StringRes;

import com.judopay.R;

/**
 * A Country that can be selected by the user when providing information for
 * address verification (AVS) checks during a transaction.
 */
public enum Country {
    UNITED_KINGDOM(R.string.united_kingdom, R.string.billing_postcode, 826),
    UNITED_STATES(R.string.united_states, R.string.billing_zip_code, 840),
    CANADA(R.string.canada, R.string.billing_postal_code, 124),
    OTHER(R.string.other_country, R.string.billing_postcode, 0);

    private final int nameResourceId;
    private final int postcodeNameResourceId;
    private final int countryCode;

    Country(@StringRes final int nameResourceId, @StringRes final int postcodeNameResourceId, final int countryCode) {
        this.nameResourceId = nameResourceId;
        this.postcodeNameResourceId = postcodeNameResourceId;
        this.countryCode = countryCode;
    }

    public int getNameResourceId() {
        return nameResourceId;
    }

    public int getPostcodeNameResourceId() {
        return postcodeNameResourceId;
    }

    public int getCountryCode() {
        return countryCode;
    }
}
