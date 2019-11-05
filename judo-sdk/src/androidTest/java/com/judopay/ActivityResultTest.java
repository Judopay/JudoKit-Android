package com.judopay;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.model.CardToken;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class ActivityResultTest {

    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Test
    public void shouldReturnTokenCardExpiredResult() {
        Judo judo = getJudo()
                .newBuilder()
                .setAmount("0.99")
                .setCardToken(new CardToken("1215", "1234", "cardToken", 1, "scheme", "funding", "category", "country", "bank"))
                .setConsumerReference("consumerRef")
                .build();

        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, judo);

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_TOKEN_EXPIRED));
        assertThat(activity, matcher);
    }
}