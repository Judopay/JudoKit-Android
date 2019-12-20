package com.judopay.theme;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ViewMatchers.isNotDisplayed;

@RunWith(AndroidJUnit4.class)
public class SecurityMessageTest {

    @Rule
    public ActivityTestRule<PaymentActivity> testRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Rule
    public ActivityTestRule<PreAuthActivity> preAuthActivityActivityTestRule = new ActivityTestRule<>(PreAuthActivity.class, false, false);

    @Test
    public void shouldDisplaySecurityMessageWhenSetInTheme() {
        preAuthActivityActivityTestRule.launchActivity(getIntent());

        onView(withId(R.id.secureServerText))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldNotDisplaySecurityMessage() {
        testRule.launchActivity(getIntent());

        onView(withId(R.id.secureServerText))
                .check(matches(isNotDisplayed()));
    }

    private Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
        return intent;
    }
}