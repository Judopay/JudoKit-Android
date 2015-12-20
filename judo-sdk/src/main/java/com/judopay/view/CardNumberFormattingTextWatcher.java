package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.judopay.model.CardType;

public class CardNumberFormattingTextWatcher implements TextWatcher {

    private final EditText editText;

    public CardNumberFormattingTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            int cardType = CardType.matchCardNumber(s.toString());
            int selectionEnd = editText.getSelectionEnd();

            String formattedCardNumber = getFormattedCardNumber(cardType, s.toString().replaceAll(" ", ""));

            editText.removeTextChangedListener(this);
            editText.setText(formattedCardNumber);
            editText.addTextChangedListener(this);

            if (formattedCardNumber.length() < selectionEnd) {
                editText.setSelection(formattedCardNumber.length());
            } else if (formattedCardNumber.length() > selectionEnd) {
                editText.setSelection(formattedCardNumber.length());
            } else {
                editText.setSelection(selectionEnd);
            }
        }
    }

    private String getFormattedCardNumber(int cardType, String number) {
        if (cardType == CardType.AMEX) {
            return PaddedNumberFormatter.format(number, CardType.AMEX_PATTERN);
        } else {
            return PaddedNumberFormatter.format(number, CardType.VISA_PATTERN);
        }
    }

}
