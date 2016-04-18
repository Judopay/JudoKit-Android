package com.judopay.validation;

import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.R;
import com.judopay.model.Country;
import com.judopay.view.SimpleTextWatcher;

import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;

public class CountryAndPostcodeValidator implements Validator {

    private static final Pattern ukPostcodePattern = Pattern.compile("\\b(GIR ?0AA|SAN ?TA1|(?:[A-PR-UWYZ](?:\\d{0,2}|[A-HK-Y]\\d|[A-HK-Y]\\d\\d|\\d[A-HJKSTUW]|[A-HK-Y]\\d[ABEHMNPRV-Y])) ?\\d[ABD-HJLNP-UW-Z]{2})\\b");
    private static final Pattern usZipCodePattern = Pattern.compile("(^\\\\d{5}$)|(^\\\\d{5}-\\\\d{4}$)");
    private static final Pattern canadaPostalCodePattern = Pattern.compile("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]");

    private final Spinner countrySpinner;
    private final EditText postcodeEditText;

    public CountryAndPostcodeValidator(Spinner countrySpinner, EditText postcodeEditText) {
        this.countrySpinner = countrySpinner;
        this.postcodeEditText = postcodeEditText;
    }

    @Override
    public Observable<Validation> onValidate() {
        return Observable.create(new Observable.OnSubscribe<Validation>() {
            @Override
            public void call(final Subscriber<? super Validation> subscriber) {
                postcodeEditText.addTextChangedListener(new SimpleTextWatcher() {
                    @Override
                    protected void onTextChanged(CharSequence text) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(getValidation(text.toString()));
                        }
                    }
                });
            }
        });
    }

    private Validation getValidation(String text) {
        String country = (String) countrySpinner.getSelectedItem();
        boolean postcodeValid = isPostcodeValid(text, country);

        boolean postcodeEntryComplete = isPostcodeLengthValid(text.replaceAll("\\s+", ""), country);
        boolean showPostcodeError = !postcodeValid && postcodeEntryComplete;
        int postcodeError = getPostcodeError(country);

        return new Validation(postcodeValid, postcodeError, showPostcodeError);
    }

    private int getPostcodeError(String country) {
        switch (country) {
            case Country.CANADA:
                return R.string.error_postcode_canada;

            case Country.UNITED_STATES:
                return R.string.error_postcode_us;

            case Country.UNITED_KINGDOM:
            default:
                return R.string.error_postcode_uk;
        }
    }

    private boolean getPostcodeLabel(String country) {
        return false;
    }

    private boolean isPostcodeLengthValid(String postcode, String country) {
        switch (country) {
            case Country.UNITED_KINGDOM:
            case Country.CANADA:
                return postcode.length() >= 6;
            case Country.UNITED_STATES:
                return postcode.length() >= 5;
        }
        return true;
    }

    private boolean isPostcodeValid(String postcode, String country) {
        switch (country) {
            case Country.UNITED_KINGDOM:
                return ukPostcodePattern.matcher(postcode).matches();
            case Country.CANADA:
                return canadaPostalCodePattern.matcher(postcode).matches();
            case Country.UNITED_STATES:
                return usZipCodePattern.matcher(postcode).matches();
            default:
                return true;
        }
    }

}
