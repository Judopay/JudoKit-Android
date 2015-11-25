package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.judopay.customer.Country;

import java.util.ArrayList;
import java.util.List;

public class CountrySpinner extends AppCompatSpinner {

    private CountrySpinnerAdapter adapter;

    public CountrySpinner(Context context) {
        super(context);
        initialise();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CountrySpinner(Context context, int mode) {
        super(context, mode);
        initialise();
    }

    public CountrySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public CountrySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    private void initialise() {
        this.adapter = new CountrySpinnerAdapter(this.getContext(), android.R.layout.simple_list_item_1, getCountries());

        this.setAdapter(this.adapter);
    }

    private List<Country> getCountries() {
        ArrayList<Country> countries = new ArrayList<>();

        countries.add(new Country(826, Country.UNITED_KINGDOM));
        countries.add(new Country(840, Country.UNITED_STATES));
        countries.add(new Country(124, Country.CANADA));
        countries.add(new Country(0, Country.OTHER));

        return countries;
    }

    public boolean isCountrySelected() {
        return !Country.OTHER.equals(getSelectedCountry().getDisplayName());
    }

    public Country getSelectedCountry() {
        return adapter.getItem(getSelectedItemPosition());
    }
}