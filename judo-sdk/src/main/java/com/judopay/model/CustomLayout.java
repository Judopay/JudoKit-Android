package com.judopay.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.judopay.error.InvalidViewTypeInLayout;
import com.judopay.view.JudoEditText;

import java.util.Arrays;
import java.util.List;

public final class CustomLayout implements Parcelable {

    public static final Parcelable.Creator<CustomLayout> CREATOR = new Parcelable.Creator<CustomLayout>() {
        @Override
        public CustomLayout createFromParcel(Parcel source) {
            return new CustomLayout(source);
        }

        @Override
        public CustomLayout[] newArray(int size) {
            return new CustomLayout[size];
        }
    };

    private final int layoutId;
    private final int cardNumberInput;
    private final int expiryDateInput;
    private final int securityCodeInput;
    private final int startDateInput;
    private final int issueNumberInput;
    private final int countrySpinner;
    private final int postcodeInput;
    private final int submitButton;

    private CustomLayout(int layoutId,
                         int cardNumberInput,
                         int expiryDateInput,
                         int securityCodeInput,
                         int startDateInput,
                         int issueNumberInput,
                         int countrySpinner,
                         int postcodeInput,
                         int submitButton) {
        this.layoutId = layoutId;
        this.cardNumberInput = cardNumberInput;
        this.expiryDateInput = expiryDateInput;
        this.securityCodeInput = securityCodeInput;
        this.startDateInput = startDateInput;
        this.issueNumberInput = issueNumberInput;
        this.countrySpinner = countrySpinner;
        this.postcodeInput = postcodeInput;
        this.submitButton = submitButton;
    }

    private CustomLayout(Parcel in) {
        this.layoutId = in.readInt();
        this.cardNumberInput = in.readInt();
        this.expiryDateInput = in.readInt();
        this.securityCodeInput = in.readInt();
        this.startDateInput = in.readInt();
        this.issueNumberInput = in.readInt();
        this.countrySpinner = in.readInt();
        this.postcodeInput = in.readInt();
        this.submitButton = in.readInt();
    }

    public void validate(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        List<Class<? extends View>> allowedViews = Arrays.asList(EditText.class, JudoEditText.class,
                AppCompatEditText.class, TextView.class, AppCompatTextView.class, ImageView.class,
                AppCompatImageView.class, Spinner.class, AppCompatSpinner.class, Button.class,
                AppCompatButton.class, TextInputLayout.class, LinearLayout.class,
                RelativeLayout.class, FrameLayout.class, ScrollView.class);

        ViewGroup parent = (ViewGroup) inflater.inflate(layoutId, null);
        validateView(allowedViews, parent);

        checkTextInputLayout(parent, cardNumberInput);
        checkTextInputLayout(parent, expiryDateInput);
        checkTextInputLayout(parent, securityCodeInput);
        checkTextInputLayout(parent, startDateInput);
        checkTextInputLayout(parent, issueNumberInput);
        checkTextInputLayout(parent, postcodeInput);
        checkButton(parent, submitButton);
        checkSpinner(parent, countrySpinner);
    }

    private void validateView(List<Class<? extends View>> allowedViews, ViewGroup parent) {
        int numChildViews = parent.getChildCount();

        for (int i = 0; i < numChildViews; i++) {
            View childView = parent.getChildAt(i);
            Class<? extends View> viewClass = childView.getClass();

            if (childView instanceof ViewGroup) {
                validateView(allowedViews, (ViewGroup) childView);
            } else if (!allowedViews.contains(viewClass)) {
                throw new InvalidViewTypeInLayout(viewClass);
            }
        }
    }

    private void checkSpinner(View view, int submitButton) {
        checkViewType(view, submitButton, Spinner.class, AppCompatSpinner.class);
    }

    private void checkButton(View view, int submitButton) {
        checkViewType(view, submitButton, Button.class, AppCompatButton.class);
    }

    private void checkTextInputLayout(View view, int id) {
        checkViewType(view, id, TextInputLayout.class);
    }

    private void checkViewType(View view, int id, Class... classes) {
        View foundView = view.findViewById(id);

        if (foundView == null) {
            throw new IllegalArgumentException();
        }

        boolean allowedViewClass = false;
        for (Class clazz : classes) {
            if (foundView.getClass().equals(clazz)) {
                allowedViewClass = true;
            }
        }

        if (!allowedViewClass) {
            throw new InvalidViewTypeInLayout(view.getClass());
        }
    }

    @LayoutRes
    public int getLayoutId() {
        return layoutId;
    }

    @IdRes
    public int getCardNumberInput() {
        return cardNumberInput;
    }

    @IdRes
    public int getSubmitButton() {
        return submitButton;
    }

    @IdRes
    public int getExpiryDateInput() {
        return expiryDateInput;
    }

    @IdRes
    public int getIssueNumberInput() {
        return issueNumberInput;
    }

    @IdRes
    public int getStartDateInput() {
        return startDateInput;
    }

    @IdRes
    public int getCountrySpinner() {
        return countrySpinner;
    }

    @IdRes
    public int getPostcodeInput() {
        return postcodeInput;
    }

    @IdRes
    public int getSecurityCodeInput() {
        return securityCodeInput;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.layoutId);
        dest.writeInt(this.cardNumberInput);
        dest.writeInt(this.expiryDateInput);
        dest.writeInt(this.securityCodeInput);
        dest.writeInt(this.startDateInput);
        dest.writeInt(this.issueNumberInput);
        dest.writeInt(this.countrySpinner);
        dest.writeInt(this.postcodeInput);
        dest.writeInt(this.submitButton);
    }

    public static class Builder {
        private int cardNumberInput;
        private int expiryDateInput;
        private int securityCodeInput;
        private int startDateInput;
        private int issueNumberInput;
        private int countrySpinner;
        private int postcodeInput;
        private int submitButton;

        public Builder() {
        }

        public Builder cardNumberInput(@IdRes int cardNumberInput) {
            this.cardNumberInput = cardNumberInput;
            return this;
        }

        public Builder expiryDateInput(@IdRes int expiryDateInput) {
            this.expiryDateInput = expiryDateInput;
            return this;
        }

        public Builder securityCodeInput(@IdRes int securityCodeInput) {
            this.securityCodeInput = securityCodeInput;
            return this;
        }

        public Builder startDateInput(@IdRes int startDateInput) {
            this.startDateInput = startDateInput;
            return this;
        }

        public Builder issueNumberInput(@IdRes int issueNumberInput) {
            this.issueNumberInput = issueNumberInput;
            return this;
        }

        public Builder countrySpinner(@IdRes int countrySpinner) {
            this.countrySpinner = countrySpinner;
            return this;
        }

        public Builder postcodeInput(@IdRes int postcodeInput) {
            this.postcodeInput = postcodeInput;
            return this;
        }

        public Builder submitButton(@IdRes int submitButton) {
            this.submitButton = submitButton;
            return this;
        }

        public CustomLayout build(@LayoutRes int layoutId) {
            return new CustomLayout(layoutId,
                    cardNumberInput,
                    expiryDateInput,
                    securityCodeInput,
                    startDateInput,
                    issueNumberInput,
                    countrySpinner,
                    postcodeInput,
                    submitButton);
        }
    }
}
