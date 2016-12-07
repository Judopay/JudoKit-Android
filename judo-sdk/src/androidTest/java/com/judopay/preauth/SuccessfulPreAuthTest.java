package com.judopay.preauth;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PreAuthActivity;
import com.judopay.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ActivityUtil.resultCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SuccessfulPreAuthTest {

    @Rule
    public ActivityTestRule<PreAuthActivity> activityTestRule = new ActivityTestRule<>(PreAuthActivity.class, false, false);

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidVisaEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        PreAuthActivity activity = activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidMaestroEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        PreAuthActivity activity = activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidAmexEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        PreAuthActivity activity = activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("3469"));

        onView(withId(R.id.button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidVisaEnteredAndAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        PreAuthActivity activity = activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TR148PA"));

        onView(withId(R.id.button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidMaestroEnteredAndAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        PreAuthActivity activity = activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("RG48NL"));

        onView(withId(R.id.button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidAmexEnteredAndAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        PreAuthActivity activity = activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("3469"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("NW67BB"));

        onView(withId(R.id.button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

}