package com.judopay.util;

import com.google.android.material.textfield.TextInputLayout;

import androidx.core.widget.NestedScrollView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ScrollToAction;
import androidx.test.espresso.matcher.BoundedMatcher;

import android.graphics.Rect;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

public class ViewMatchers {
    public static final Long DEFAULT_TIMEOUT = 3_000L;
    public static final Long DEFAULT_SLEEP_INTERVAL = 100L;

    public static Matcher<View> withTextInputHint(String hintText) {
        checkNotNull(hintText);
        return withTextInputHint(is(hintText));
    }

    private static Matcher<View> withTextInputHint(final Matcher<String> stringMatcher) {
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

    public static Matcher<View> withResourceName(String resourceName) {
        return withResourceName(is(resourceName));
    }

    public static Matcher<View> withResourceName(final Matcher<String> resourceNameMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with resource name: ");
                resourceNameMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                int id = view.getId();
                return id != View.NO_ID && id != 0 && view.getResources() != null
                        && resourceNameMatcher.matches(view.getResources().getResourceName(id));
            }
        };
    }

    public static Matcher<View> withActionBarTitle(int resourceId) {
        return allOf(isDescendantOfA(withResourceName("com.judopay.test:id/action_bar")), withText(resourceId));
    }

    public static Matcher<View> isNotDisplayed() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is displayed on the screen to the user");
            }

            @Override
            public boolean matchesSafely(View view) {
                return withEffectiveVisibility(androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE).matches(view);
            }
        };
    }

    public static Matcher<View> isOpaque() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is opaque");
            }

            @Override
            public boolean matchesSafely(View view) {
                return view.getAlpha() == 1.0f;
            }
        };
    }

    public static Matcher<View> isTranslucent() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is opaque");
            }

            @Override
            public boolean matchesSafely(View view) {
                return view.getAlpha() == 0.5f;
            }
        };
    }

    public static Matcher<View> isDisabled() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is disabled");
            }

            @Override
            public boolean matchesSafely(View view) {
                return !view.isEnabled();
            }
        };
    }

    private static void checkNotNull(Object object) {
        if (object == null) throw new IllegalArgumentException();
    }


    public static ViewAction timeout() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return "wait until View is visible for: " + DEFAULT_TIMEOUT + " ml";
            }

            @Override
            public void perform(UiController uiController, View view) {
                Long counter = DEFAULT_TIMEOUT;
                while (counter > 0) {
                    if (view.getGlobalVisibleRect(new Rect()) && androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE).matches(view)) {
                        return;
                    } else {
                        counter -= DEFAULT_SLEEP_INTERVAL;
                        try {
                            TimeUnit.MILLISECONDS.sleep(DEFAULT_SLEEP_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    public static ViewAction scrollTo() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE),
                        androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA(anyOf(
                                androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom(ScrollView.class),
                                androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom(HorizontalScrollView.class),
                                androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom(NestedScrollView.class)
                        )));
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public void perform(UiController uiController, View view) {
                new ScrollToAction().perform(uiController, view);
            }
        };
    }

}