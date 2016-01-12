package com.judopay.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.judopay.R;
import com.judopay.model.Country;

import java.util.List;

class CountrySpinnerAdapter extends ArrayAdapter<Country> {

    public CountrySpinnerAdapter(Context context, List<Country> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.dropdown_item, parent, false);

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(R.string.billing_country);

        TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
        textView2.setText(getItem(position).getDisplayName());

        return view;
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
