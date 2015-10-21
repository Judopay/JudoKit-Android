package com.judopay.payment.form;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.judopay.R;
import com.judopay.customer.Country;

import java.util.List;

public class CountrySpinnerAdapter extends ArrayAdapter<Country> {

    public CountrySpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CountrySpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CountrySpinnerAdapter(Context context, int resource, Country[] objects) {
        super(context, resource, objects);
    }

    public CountrySpinnerAdapter(Context context, int resource, int textViewResourceId, Country[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public CountrySpinnerAdapter(Context context, int resource, List<Country> objects) {
        super(context, resource, objects);
    }

    public CountrySpinnerAdapter(Context context, int resource, int textViewResourceId, List<Country> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewWithCountry(position, parent, R.layout.support_simple_spinner_dropdown_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewWithCountry(position, parent, R.layout.support_simple_spinner_dropdown_item);
    }

    @NonNull
    private View getViewWithCountry(int position, ViewGroup parent, int layout) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layout, parent, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(getItem(position).getDisplayName());

        return view;
    }

}
