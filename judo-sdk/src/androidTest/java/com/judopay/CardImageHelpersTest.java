package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ViewMatchers.isOpaque;
import static com.judopay.util.ViewMatchers.isTranslucent;

@RunWith(AndroidJUnit4.class)
public class CardImageHelpersTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldShowCardTypeImageAsOpaqueAfterCardNumberEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(click());

        onView(withId(R.id.card_type_view))
                .check(matches(isOpaque()));
    }

    @Test
    public void shouldShowSecurityCodeImageAsTranslucentWhenNotFocused() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(click());

        onView(withId(R.id.security_code_image_view))
                .check(matches(isTranslucent()));
    }

    @Test
    public void shouldShowCardImageAsTranslucentAfterDeletedAndNotFocused() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976"))
                .perform(replaceText(""));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(click());

        onView(withId(R.id.card_type_view))
                .check(matches(isTranslucent()));
    }

}
