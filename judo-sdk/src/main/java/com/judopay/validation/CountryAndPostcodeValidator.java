package com.judopay.validation;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.R;
import com.judopay.model.Country;
import com.judopay.view.SimpleTextWatcher;

import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.observables.ConnectableObservable;

import static com.judopay.arch.TextUtil.isEmpty;
import static java.util.regex.Pattern.compile;

public class CountryAndPostcodeValidator implements Validator {
    private static final Pattern UK_POSTCODE_PATTERN = compile("\\b(GIR ?0AA|SAN ?TA1|(?:[A-PR-UWYZ](?:\\d{0,2}|[A-HK-Y]\\d|[A-HK-Y]\\d\\d|\\d[A-HJKSTUW]|[A-HK-Y]\\d[ABEHMNPRV-Y])) ?\\d[ABD-HJLNP-UW-Z]{2})\\b");
    private static final Pattern US_ZIPCODE_PATTERN = compile("^\\d{5}(?:[-\\s]\\d{4})?$");
    private static final Pattern CANADA_POSTAL_CODE_PATTERN = compile("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]");

    private final Spinner countrySpinner;
    private final EditText postcodeEditText;

    public CountryAndPostcodeValidator(Spinner countrySpinner, EditText postcodeEditText) {
        this.countrySpinner = countrySpinner;
        this.postcodeEditText = postcodeEditText;
    }

    @Override
    public ConnectableObservable<Validation> onValidate() {
        return Observable.create((ObservableEmitter<Validation> emitter) -> {
            postcodeEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                protected void onTextChanged(CharSequence text) {
                    emitter.onNext(getValidation(text.toString()));
                }
            });

            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    emitter.onNext(getValidation(postcodeEditText.getText().toString()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }).publish();
    }

    private Validation getValidation(String text) {
        String country = (String) countrySpinner.getSelectedItem();
        boolean postcodeValid = Country.OTHER.equals(country) || (!isEmpty(text) && isPostcodeValid(text, country));

        boolean postcodeEntryComplete = !isEmpty(text) && isPostcodeLengthValid(text.replaceAll("\\s+", ""), country);
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
                return UK_POSTCODE_PATTERN.matcher(postcode).matches();
            case Country.CANADA:
                return CANADA_POSTAL_CODE_PATTERN.matcher(postcode).matches();
            case Country.UNITED_STATES:
                return US_ZIPCODE_PATTERN.matcher(postcode).matches();
            default:
                return true;
        }
    }
}
