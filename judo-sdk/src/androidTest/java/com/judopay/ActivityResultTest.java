package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.model.CardToken;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ActivityUtil.resultCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ActivityResultTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldReturnTokenCardExpiredResult() {
        Intent intent = new Intent();
        Judo judo = getJudo()
                .newBuilder()
                .setAmount("0.99")
                .setCardToken(new CardToken("1215", "1234", "cardToken", 1))
                .setConsumerReference("consumerRef")
                .build());

        intent.putExtra(Judo.JUDO_OPTIONS, judo);

        PaymentActivity activity = activityTestRule.launchActivity(intent);

        assertThat(resultCode(activity), is(Judo.RESULT_TOKEN_EXPIRED));
    }

}
