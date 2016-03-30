package com.judopay.util;

import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.is;

public class ViewMatchers {

    public static Matcher<View> withTextInputHint(String hintText) {
        checkNotNull(hintText);
        return withTextInputHint(is(hintText));
    }

    public static Matcher<View> withTextInputHint(final Matcher<String> stringMatcher) {
        checkNotNull(stringMatcher);
        return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with hint: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextInputLayout textView) {
                return stringMatcher.matches(textView.getHint());
            }
        };
    }

    private static void checkNotNull(Object object) {
        if (object == null) throw new IllegalArgumentException();
    }

}
