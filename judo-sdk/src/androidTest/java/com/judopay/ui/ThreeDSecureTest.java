package com.judopay.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.web.model.Atom;
import android.support.test.espresso.web.model.ElementReference;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.webkit.WebView;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PreAuthActivity;
import com.judopay.R;
import com.judopay.model.Currency;
import com.judopay.util.WebViewIdlingResource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ThreeDSecureTest {

    @Rule
    public ActivityTestRule<PreAuthActivity> activityTestRule = new ActivityTestRule<>(PreAuthActivity.class, false, false);

    private WebViewIdlingResource webViewIdlingResource;

    @Before
    public void setEnvironment() {
        Judo.setEnvironment(Judo.UAT);
    }

    @Test
    public void shouldShow3dSecureDialog() {
        final PreAuthActivity activity = activityTestRule.launchActivity(getIntent());

        registerWebViewIdlingResource(activity);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976350000006891"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("341"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withId(R.id.three_d_secure_web_view))
                .check(matches(isDisplayed()));

        Atom<ElementReference> submitButton = findElement(Locator.CLASS_NAME, "ACSSubmit");
        onWebView().withElement(submitButton)
                .perform(webClick());

        unregisterIdlingResources(webViewIdlingResource);
    }

    public void registerWebViewIdlingResource(final Activity activity) {
        UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();
        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webViewIdlingResource = new WebViewIdlingResource((WebView) activity.findViewById(R.id.three_d_secure_web_view));
                    Espresso.registerIdlingResources(webViewIdlingResource);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void unregisterWebViewIdlingResource() {
        Espresso.unregisterIdlingResources(webViewIdlingResource);
    }

    private Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100915867")
                .setAmount("0.01")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString())
                .build());

        return intent;
    }

}