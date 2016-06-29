package com.judopay.token;

import android.content.Intent;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.model.Address;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;
import com.judopay.receipts.RxHelpers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.receipts.RxHelpers.failOnError;
import static com.judopay.util.ActivityUtil.resultCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith(AndroidJUnit4.class)
public class SuccessfulTokenPaymentTest {

    @Rule
    public ActivityTestRule<PaymentActivity> tokenPaymentActivityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES);
    }

    @Test
    public void shouldBeSuccessfulTokenPayment() {
        Judo judo = getJudo().build();
        final JudoApiService apiService = judo.getApiService(getContext());

        RegisterCardRequest registerCardRequest = new RegisterCardRequest.Builder()
                .setJudoId("100915867")
                .setCardNumber("4976000000003436")
                .setExpiryDate("12/20")
                .setCv2("452")
                .setYourConsumerReference(UUID.randomUUID().toString())
                .build();

        apiService.registerCard(registerCardRequest)
                .compose(RxHelpers.<Receipt>schedulers())
                .subscribe(new Action1<Receipt>() {
                    @Override
                    public void call(Receipt receipt) {
                        Intent intent = new Intent();
                        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                                .setCardToken(receipt.getCardDetails())
                                .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                                .build());

                        PaymentActivity activity = tokenPaymentActivityTestRule.launchActivity(intent);

                        onView(withId(R.id.security_code_edit_text))
                                .perform(typeText("452"));

                        onView(withId(R.id.button))
                                .perform(click());

                        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
                    }
                }, failOnError());
    }

    @Test
    public void shouldBeSuccessfulTokenPaymentWhenAvsEnabled() {
        final JudoApiService apiService = getJudo()
                .setAvsEnabled(true)
                .build()
                .getApiService(getContext());

        RegisterCardRequest registerCardRequest = new RegisterCardRequest.Builder()
                .setJudoId("100915867")
                .setCardNumber("4976000000003436")
                .setExpiryDate("12/20")
                .setCv2("452")
                .setYourConsumerReference(UUID.randomUUID().toString())
                .setCardAddress(new Address.Builder()
                        .setPostCode("TR148PA")
                        .setCountryCode(826)
                        .build())
                .build();

        apiService.registerCard(registerCardRequest)
                .compose(RxHelpers.<Receipt>schedulers())
                .subscribe(new Action1<Receipt>() {
                    @Override
                    public void call(Receipt receipt) {
                        Intent intent = new Intent();
                        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                                .setAvsEnabled(true)
                                .setCardToken(receipt.getCardDetails())
                                .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                                .build());

                        PaymentActivity activity = tokenPaymentActivityTestRule.launchActivity(intent);

                        onView(withId(R.id.security_code_edit_text))
                                .perform(typeText("452"));

                        onView(withId(R.id.post_code_edit_text))
                                .perform(typeText("TR148PA"));

                        onView(withId(R.id.button))
                                .perform(click());

                        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
                    }
                }, failOnError());
    }

    private Judo.Builder getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP);
    }

}