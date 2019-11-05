package com.judopay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import static com.judopay.Judo.IDEAL_PAYMENT;

public class IdealPaymentActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.ideal_payment));
        setContentView(R.layout.activity_ideal_payment);
    }

    public static void openIdealScreen(Activity activity, Judo judo) {
        Intent intent = new Intent(activity, IdealPaymentActivity.class);
        intent.putExtra(Judo.GPAY_PREAUTH, false);
        intent.putExtra(Judo.JUDO_OPTIONS, judo);
        activity.startActivityForResult(intent, IDEAL_PAYMENT);
    }
}
