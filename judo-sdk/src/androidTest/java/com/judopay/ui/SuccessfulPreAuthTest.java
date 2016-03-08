package com.judopay.ui;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PreAuthActivity;
import com.judopay.R;
import com.judopay.model.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.ui.util.ActivityUtil.resultCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SuccessfulPreAuthTest {

    @Rule
    public ActivityTestRule<PreAuthActivity> activityTestRule = new ActivityTestRule<>(PreAuthActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.SANDBOX);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidVisaEntered() {
        activityTestRule.launchActivity(getIntent());
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidVisaEntered() {
        Judo.setAvsEnabled(false);

        PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidMaestroEntered() {
        Judo.setAvsEnabled(false);
        Judo.setMaestroEnabled(true);

        PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidAmexEntered() {
        Judo.setAvsEnabled(false);
        Judo.setAmexEnabled(true);

        PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("3469"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidVisaEnteredAndAvsEnabled() {
        Judo.setAvsEnabled(true);

        PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TR148PA"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidMaestroEnteredAndAvsEnabled() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(true);

        PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("RG48NL"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidAmexEnteredAndAvsEnabled() {
        Judo.setAvsEnabled(true);
        Judo.setAmexEnabled(true);

        PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("3469"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("NW67BB"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    protected Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .build());

        return intent;
    }

}
