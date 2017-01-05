package com.judopay.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.judopay.R;
import com.judopay.model.CardNetwork;
import com.judopay.validation.Validation;

/**
 * A view that allows for the security code of a card (CV2, CID) to be input and an image displayed to
 * indicate where on the payment card the security code can be located.
 */
public class SecurityCodeEntryView extends RelativeLayout {

    private static final String KEY_SUPER_STATE = "superState";
    private static final String KEY_CARD_TYPE = "cardType";

    private JudoEditText editText;
    private CardSecurityCodeView imageView;
    private TextInputLayout inputLayout;
    private HintFocusListener hintFocusListener;
    private TextView helperText;

    private int cardType;

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
        inflater.inflate(R.layout.view_security_code_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editText = (JudoEditText) findViewById(R.id.security_code_edit_text);
        inputLayout = (TextInputLayout) findViewById(R.id.security_code_input_layout);
        imageView = (CardSecurityCodeView) findViewById(R.id.security_code_image_view);
        helperText = (TextView) findViewById(R.id.security_code_helper_text);

        hintFocusListener = new HintFocusListener(editText, "000");

        editText.setOnFocusChangeListener(new MultiOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(helperText),
                new ViewAlphaChangingTextWatcher(editText, imageView),
                hintFocusListener
        ));
        editText.addTextChangedListener(new HidingViewTextWatcher(helperText));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(KEY_CARD_TYPE, cardType);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable(KEY_SUPER_STATE);

            setCardType(bundle.getInt(KEY_CARD_TYPE), false);
        }
        super.onRestoreInstanceState(state);
    }

    public void setHelperText(@StringRes int resId) {
        helperText.setText(resId);
    }

    public void setText(CharSequence text) {
        editText.setText(text);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        editText.addTextChangedListener(watcher);
    }

    public void setCardType(int cardType, boolean animate) {
        this.cardType = cardType;

        imageView.setImageType(cardType, animate);

        setHint(CardNetwork.securityCode(cardType));

        setAlternateHint(CardNetwork.securityCodeHint(cardType));

        setMaxLength(CardNetwork.securityCodeLength(cardType));
    }

    private void setMaxLength(int length) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    private void setHint(String hint) {
        inputLayout.setHint(hint);
    }

    private void setAlternateHint(String hint) {
        hintFocusListener.setHint(hint);
    }

    public String getText() {
        return editText.getText().toString().trim();
    }

    public JudoEditText getEditText() {
        return editText;
    }

    public void setValidation(Validation validation) {
        inputLayout.setErrorEnabled(validation.isShowError());

        if (validation.isShowError()) {
            inputLayout.setError(getResources().getString(validation.getError()));
        } else {
            inputLayout.setError("");
        }
    }
}