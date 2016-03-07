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
import com.judopay.model.CardType;

/**
 * A view that allows for card number data to be input by the user and the detected card type
 * to be displayed alongside the card number.
 * Does not perform validation itself, this is done by the {@link com.judopay.CardNumberValidation}
 * class.
 */
public class CardNumberEntryView extends RelativeLayout {

    private EditText cardNumberEditText;
    private CardTypeImageView cardTypeImageView;
    private TextInputLayout cardNumberInputLayout;

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
        this.cardNumberEditText = (EditText) findViewById(R.id.card_number_edit_text);
        this.cardNumberInputLayout = (TextInputLayout) findViewById(R.id.card_number_input_layout);

        cardNumberEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(cardNumberHelperText),
                new ViewAlphaChangingTextWatcher(cardNumberEditText, cardTypeImageView),
                new HintFocusListener(cardNumberEditText, R.string.card_number_hint)
        ));

        cardNumberEditText.addTextChangedListener(new CardNumberFormattingTextWatcher());
        cardNumberEditText.addTextChangedListener(new HidingViewTextWatcher(cardNumberHelperText));
    }

    public void setCardType(int type) {
        cardTypeImageView.setCardType(type);
    }

    public void setText(String text) {
        cardNumberEditText.setText(text);
    }

    public void setMaxLength(int maxLength) {
        cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public int getCardType() {
        return CardType.fromCardNumber(cardNumberEditText.getText().toString());
    }

    public String getText() {
        return cardNumberEditText.getText().toString().replaceAll(" ", "");
    }

    public void addTextChangedListener(SimpleTextWatcher watcher) {
        cardNumberEditText.addTextChangedListener(watcher);
    }

    public void setTokenizedNumber(String lastFour) {
        cardNumberEditText.setEnabled(false);
        cardNumberEditText.setText(getContext().getString(R.string.token_card_number, lastFour));
    }

    public void setError(@StringRes int message, boolean show) {
        cardNumberInputLayout.setErrorEnabled(show);

        if (show) {
            cardNumberInputLayout.setError(getResources().getString(message));
        } else {
            cardNumberInputLayout.setError("");
        }
    }
}