package com.judopay;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.judopay.model.Bank;
import com.judopay.view.custom.DefaultCustomTextView;

public class BankAdapter extends ArrayAdapter<Bank> {
    private final static int FIRST_ELEMENT = 0;

    BankAdapter(final Context context, final Bank[] banks) {
        super(context, R.layout.item_bank_spinner, banks);
    }

    @Override
    public View getDropDownView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        if (position == FIRST_ELEMENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blank_spinner, parent, false);
            view.findViewById(R.id.bank_text).setVisibility(View.GONE);
            return view;
        } else {
            View view = getBankItemView(position, parent);
            ImageView bankImageView = view.findViewById(R.id.bank_image);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bankImageView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            return view;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        if (position == FIRST_ELEMENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blank_spinner, parent, false);
            DefaultCustomTextView bankText = view.findViewById(R.id.bank_text);
            IdealCustomTheme theme = IdealCustomTheme.getInstance();
            bankText.setCustomText(theme.getBankHint(), theme.getFontSize(), theme.getTextColor(), theme.getTypeface());
            return view;
        } else {
            return getBankItemView(position, parent);
        }
    }

    private View getBankItemView(final int position, @NonNull final ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_spinner, parent, false);
        ImageView bankImage = view.findViewById(R.id.bank_image);
        Bank bank = getItem(position);
        bankImage.setContentDescription(getContext().getString(bank.getTitleResourceId()));
        bankImage.setImageResource(bank.getLogoResourceId());
        return view;
    }
}
