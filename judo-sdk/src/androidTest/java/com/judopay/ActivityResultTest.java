package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.model.CardToken;
import com.judopay.model.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

        intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
                .setEnvironment(Judo.SANDBOX)
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setCardToken(new CardToken("1215", "1234", "cardToken", 1))
                .setConsumerRef("consumerRef")
                .build());

        PaymentActivity activity = activityTestRule.launchActivity(intent);

        assertThat(resultCode(activity), is(Judo.RESULT_TOKEN_EXPIRED));
    }

}
