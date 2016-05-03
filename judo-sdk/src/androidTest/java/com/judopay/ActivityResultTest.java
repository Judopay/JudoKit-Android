package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.TokenPaymentActivity;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.judopay.util.ActivityUtil.resultCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ActivityResultTest {

    @Rule
    public ActivityTestRule<TokenPaymentActivity> activityTestRule = new ActivityTestRule<>(TokenPaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.UAT);
    }

    @Test
    public void shouldReturnTokenCardExpiredResult() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setCardToken(new CardToken("1215", "1234", "cardToken", 1))
                .setConsumerRef("consumerRef")
                .build());

        TokenPaymentActivity activity = activityTestRule.launchActivity(intent);

        assertThat(resultCode(activity), is(Judo.RESULT_TOKEN_EXPIRED));
    }

}
