package com.judopay.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.judopay.R;
import com.judopay.model.Country;

public class CountrySpinnerAdapter extends ArrayAdapter<Country> {

    public CountrySpinnerAdapter(final Context context, final Country[] countries) {
        super(context, android.R.layout.simple_list_item_1, countries);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        @SuppressLint("ViewHolder")
        final View view = inflater.inflate(R.layout.dropdown_item, parent, false);

        final TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(R.string.billing_country);

        final TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
        textView2.setText(getItem(position).getNameResourceId());

        return view;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        return getViewWithCountry(position, parent, R.layout.support_simple_spinner_dropdown_item);
    }

    private View getViewWithCountry(final int position, final ViewGroup parent, final int layout) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View view = inflater.inflate(layout, parent, false);
        final TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(getItem(position).getNameResourceId());

        return view;
    }

}