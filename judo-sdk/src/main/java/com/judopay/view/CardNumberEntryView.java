package com.judopay.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.judopay.R;
import com.judopay.model.CardToken;
import com.judopay.model.CardType;

import static com.judopay.model.CardType.AMEX;

/**
 * A view that allows for card number data to be input by the user and the detected card type
 * to be displayed alongside the card number.
 * Does not perform validation itself, this is done by the {@link com.judopay.CardNumberValidation}
 * class.
 */
public class CardNumberEntryView extends RelativeLayout {

    private EditText editText;
    private TextInputLayout inputLayout;
    private CardTypeImageView cardTypeImageView;
    private NumberFormatTextWatcher numberFormatTextWatcher;

    public CardNumberEntryView(Context context) {
        super(context);
        initialize(context);
    }

    public CardNumberEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CardNumberEntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_card_number_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.cardTypeImageView = (CardTypeImageView) findViewById(R.id.card_type_view);
        TextView cardNumberHelperText = (TextView) findViewById(R.id.card_number_helper_text);
        this.editText = (EditText) findViewById(R.id.card_number_edit_text);
        this.inputLayout = (TextInputLayout) findViewById(R.id.card_number_input_layout);

        editText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(cardNumberHelperText),
                new ViewAlphaChangingTextWatcher(editText, cardTypeImageView),
                new HintFocusListener(editText, R.string.card_number_format)
        ));

        numberFormatTextWatcher = new NumberFormatTextWatcher(editText, getResources().getString(R.string.card_number_format));
        editText.addTextChangedListener(numberFormatTextWatcher);
        editText.addTextChangedListener(new HidingViewTextWatcher(cardNumberHelperText));
    }

    public void setCardType(int type, boolean animate) {
        cardTypeImageView.setCardType(type, animate);

        switch (type) {
            case CardType.AMEX:
                setMaxLength(17);
                numberFormatTextWatcher.setFormat(getResources().getString(R.string.amex_card_number_format));
                break;

            default:
                setMaxLength(19);
                numberFormatTextWatcher.setFormat(getResources().getString(R.string.card_number_format));
        }
    }

    public void setText(String text) {
        editText.setText(text);
    }

    private void setMaxLength(int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public int getCardType() {
        return CardType.fromCardNumber(editText.getText().toString());
    }

    public String getText() {
        return editText.getText().toString().replaceAll(" ", "");
    }

    public void addTextChangedListener(SimpleTextWatcher watcher) {
        editText.addTextChangedListener(watcher);
    }

    public void setTokenCard(CardToken cardToken) {
        editText.setEnabled(false);
        boolean amex = cardToken.getType() == AMEX;

        editText.removeTextChangedListener(numberFormatTextWatcher);
        editText.setText(getContext().getString(amex ? R.string.amex_token_card_number : R.string.token_card_number, cardToken.getLastFour()));
        editText.addTextChangedListener(numberFormatTextWatcher);

        cardTypeImageView.setAlpha(1.0f);
    }

    public void setError(@StringRes int message, boolean show) {
        inputLayout.setErrorEnabled(show);

        if (show) {
            inputLayout.setError(getResources().getString(message));
        } else {
            inputLayout.setError("");
        }
    }
}