package com.judopay.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.judopay.R;
import com.judopay.model.CardType;

/**
 * A view that allows for the CVV (CV2/CIDV) of a card to be input and an image displayed to
 * indicate where on the payment card the CVV number can be located.
 */
public class CvvEntryView extends RelativeLayout {

    private EditText cvvEditText;
    private CvvImageView cvvImageView;
    private TextInputLayout cvvInputLayout;
    private HintFocusListener cvvHintChangeListener;

    public CvvEntryView(Context context) {
        super(context);
        initialize(context);
    }

    public CvvEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CvvEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_cvv_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        cvvEditText = (EditText) findViewById(R.id.cvv_edit_text);
        cvvInputLayout = (TextInputLayout) findViewById(R.id.cvv_input_layout);
        View cvvHelperText = findViewById(R.id.cvv_helper_text);
        cvvImageView = (CvvImageView) findViewById(R.id.cvv_image_view);

        cvvHintChangeListener = new HintFocusListener(cvvEditText, R.string.cvv_hint);

        cvvEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(cvvHelperText),
                cvvHintChangeListener
        ));
        cvvEditText.addTextChangedListener(new HidingViewTextWatcher(cvvHelperText));
        cvvEditText.addTextChangedListener(new ViewAlphaChangingTextWatcher(cvvImageView));
    }

    public void setText(CharSequence text) {
        cvvEditText.setText(text);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        cvvEditText.addTextChangedListener(watcher);
    }

    public void setCardType(int cardType) {
        cvvImageView.setCardType(cardType);

        if(CardType.AMEX == cardType) {
            setHint(R.string.amex_cvv_label);
            setAlternateHint(R.string.amex_cvv_hint);
        } else {
            setHint(R.string.cvv_label);
            setAlternateHint(R.string.cvv_hint);
        }
    }

    public void setMaxLength(int length) {
        cvvEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    public void setHint(@StringRes int hintResId) {
        cvvInputLayout.setHint(getResources().getString(hintResId));
    }

    public void setAlternateHint(@StringRes int hintResId) {
        cvvHintChangeListener.setHintResourceId(hintResId);
    }

    public String getText() {
        return cvvEditText.getText().toString().trim();
    }

}
