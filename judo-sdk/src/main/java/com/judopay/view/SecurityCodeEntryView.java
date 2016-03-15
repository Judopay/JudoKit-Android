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
 * A view that allows for the security code of a card (CV2, CID) to be input and an image displayed to
 * indicate where on the payment card the security code can be located.
 */
public class SecurityCodeEntryView extends RelativeLayout {

    private EditText cvvEditText;
    private CardSecurityCodeView securityCodeView;
    private TextInputLayout cvvInputLayout;
    private HintFocusListener cvvHintChangeListener;

    public SecurityCodeEntryView(Context context) {
        super(context);
        initialize(context);
    }

    public SecurityCodeEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SecurityCodeEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_card_security_code, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        cvvEditText = (EditText) findViewById(R.id.cvv_edit_text);
        cvvInputLayout = (TextInputLayout) findViewById(R.id.cvv_input_layout);
        View cvvHelperText = findViewById(R.id.cvv_helper_text);
        securityCodeView = (CardSecurityCodeView) findViewById(R.id.cvv_image_view);

        cvvHintChangeListener = new HintFocusListener(cvvEditText, R.string.cvv_hint);

        cvvEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(cvvHelperText),
                new ViewAlphaChangingTextWatcher(cvvEditText, securityCodeView),
                cvvHintChangeListener
        ));
        cvvEditText.addTextChangedListener(new HidingViewTextWatcher(cvvHelperText));
    }

    public void setText(CharSequence text) {
        cvvEditText.setText(text);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        cvvEditText.addTextChangedListener(watcher);
    }

    public void setCardType(int cardType) {
        securityCodeView.setCardType(cardType);

        if (CardType.AMEX == cardType) {
            setAlternateHint(R.string.amex_cvv_hint);
        } else {
            setAlternateHint(R.string.cvv_hint);
        }
        setHint(getSecurityCodeLabel(cardType));
    }

    private static int getSecurityCodeLabel(int cardType) {
        switch (cardType) {
            case CardType.AMEX:
                return R.string.cid;
            case CardType.VISA:
                return R.string.cvv2;
            case CardType.MASTERCARD:
                return R.string.cvc2;
            case CardType.CHINA_UNION_PAY:
                return R.string.cvn2;
            case CardType.JCB:
                return R.string.cav2;
            default:
                return R.string.cvv;
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