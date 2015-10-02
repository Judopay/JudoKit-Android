package com.judopay.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class CountrySpinnerAdapter extends ArrayAdapter<Locale> {

    public CountrySpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CountrySpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CountrySpinnerAdapter(Context context, int resource, Locale[] objects) {
        super(context, resource, objects);
    }

    public CountrySpinnerAdapter(Context context, int resource, int textViewResourceId, Locale[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public CountrySpinnerAdapter(Context context, int resource, List<Locale> objects) {
        super(context, resource, objects);
    }

    public CountrySpinnerAdapter(Context context, int resource, int textViewResourceId, List<Locale> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewWithCountry(position, parent, android.R.layout.simple_dropdown_item_1line);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewWithCountry(position, parent, android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    private View getViewWithCountry(int position, ViewGroup parent, int layout) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layout, parent, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(getItem(position).getDisplayCountry());

        return view;
    }

}
