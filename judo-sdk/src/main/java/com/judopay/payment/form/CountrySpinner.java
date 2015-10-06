package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.judopay.customer.Country;

import java.util.ArrayList;
import java.util.List;

public class CountrySpinner extends Spinner {

    public static final String OTHER_COUNTRY = "Other";
    private CountrySpinnerAdapter adapter;
    private Listener listener;

    public interface Listener {
        void onCountrySelected();
        void onOtherCountrySelected();
    }

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

        super.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country country = adapter.getItem(position);
                if (listener != null) {
                    if (OTHER_COUNTRY.equals(country.getDisplayName())) {
                        listener.onOtherCountrySelected();
                    } else {
                        listener.onCountrySelected();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<Country> getCountries() {
        ArrayList<Country> countries = new ArrayList<>();

        countries.add(new Country(826, "United Kingdom"));
        countries.add(new Country(840, "United States"));
        countries.add(new Country(124, "Canada"));
        countries.add(new Country(0, OTHER_COUNTRY));

        return countries;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Country getSelectedCountry() {
        return adapter.getItem(getSelectedItemPosition());
    }
}