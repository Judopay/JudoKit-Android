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

    public CountryAndPostcodeValidator(final Spinner countrySpinner, final EditText postcodeEditText) {
        this.countrySpinner = countrySpinner;
        this.postcodeEditText = postcodeEditText;
    }

    @Override
    public ConnectableObservable<Validation> onValidate() {
        return Observable.create((ObservableEmitter<Validation> emitter) -> {
            postcodeEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                protected void onTextChanged(final CharSequence text) {
                    emitter.onNext(getValidation(text.toString().toUpperCase()));
                }
            });

            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                    emitter.onNext(getValidation(postcodeEditText.getText().toString().toUpperCase()));
                }

                @Override
                public void onNothingSelected(final AdapterView<?> parent) {
                }
            });
        }).publish();
    }

    private Validation getValidation(final String text) {
        final Country country = (Country) countrySpinner.getSelectedItem();
        final boolean postcodeValid = Country.OTHER.equals(country) || (!isEmpty(text) && isPostcodeValid(text, country));

        final boolean postcodeEntryComplete = !isEmpty(text) && isPostcodeLengthValid(text.replaceAll("\\s+", ""), country);
        final boolean showPostcodeError = !postcodeValid && postcodeEntryComplete;
        final int postcodeError = getPostcodeError(country);

        return new Validation(postcodeValid, postcodeError, showPostcodeError);
    }

    private int getPostcodeError(final Country country) {
        switch (country) {
            case CANADA:
                return R.string.error_postcode_canada;

            case UNITED_STATES:
                return R.string.error_postcode_us;

            case UNITED_KINGDOM:
            default:
                return R.string.error_postcode_uk;
        }
    }

    private boolean isPostcodeLengthValid(final String postcode, final Country country) {
        switch (country) {
            case UNITED_KINGDOM:
            case CANADA:
                return postcode.length() >= 6;
            case UNITED_STATES:
                return postcode.length() >= 5;
        }
        return true;
    }

    private boolean isPostcodeValid(final String postcode, final Country country) {
        switch (country) {
            case UNITED_KINGDOM:
                return UK_POSTCODE_PATTERN.matcher(postcode).matches();
            case CANADA:
                return CANADA_POSTAL_CODE_PATTERN.matcher(postcode).matches();
            case UNITED_STATES:
                return US_ZIPCODE_PATTERN.matcher(postcode).matches();
            default:
                return true;
        }
    }
}
