package com.judopay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.gson.Gson;
import com.judopay.arch.GooglePaymentUtils;
import com.judopay.model.GooglePayRequest;
import com.judopay.model.Receipt;

import java.io.Reader;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Displays a screen with different payment methods Judopay supports.
 *
 * To launch the PaymentMethodActivity, {@link android.app.Activity#startActivityForResult(Intent, int)}
 * with an Intent containing the configuration options:
 *
 * <pre class="prettyprint">
 * Intent intent = new Intent(this, PaymentMethodActivity.class);
 * intent.putExtra(Judo.GPAY_PREAUTH, true)
 * intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
 * .setJudoId("1234567")
 * .setCurrency(Currency.GBP)
 * .setAmount("1.99")
 * .setConsumerReference("consumerRef")
 * .setPaymentMethod(EnumSet.of(PaymentMethod.CREATE_PAYMENT, PaymentMethod.GPAY_PAYMENT))
 * .build());
 *
 * startActivityForResult(intent, PAYMENT_METHOD);
 * </pre>
 *
 * See {@link Judo} for the full list of supported options
 */
public class PaymentMethodActivity extends BaseActivity {

    private static final String TAG = PaymentMethodActivity.class.getSimpleName();
    private Disposable disposable = new CompositeDisposable();
    private boolean isGPayPreAuth;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.judo_payment_method);
        isGPayPreAuth = getIntent().getBooleanExtra(Judo.GPAY_PREAUTH, false);

        if (savedInstanceState == null) {
            Fragment paymentMethodFragment = PaymentMethodFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, paymentMethodFragment, null)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Judo.GPAY_REQUEST) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    if (paymentData != null) {
                        GooglePayRequest googlePayRequest = GooglePaymentUtils.createGooglePayRequest(getJudo(), paymentData);
                        finishGPayRequest(googlePayRequest);
                    } else {
                        Log.e(TAG, "LoadPaymentData failed. No payment data found.");
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    // Nothing to do here normally - the user simply cancelled without selecting a
                    // payment method.
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    if (status != null) {
                        Intent intent = new Intent();
                        intent.putExtra(Judo.GPAY_STATUS, status);
                        setResult(Judo.GPAY_ERROR_RESULT, intent);
                    }
                    break;
            }
        } else {
            if (resultCode != Judo.RESULT_CANCELED) {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    private void finishGPayRequest(final GooglePayRequest googlePayRequest) {
        final JudoApiService apiService = getJudo().getApiService(this);
        Single<Receipt> apiRequest;
        if (isGPayPreAuth) {
            apiRequest = apiService.googlePayPreAuth(googlePayRequest);
        } else {
            apiRequest = apiService.googlePayPayment(googlePayRequest);
        }

        disposable = apiRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(receipt -> {
                    Intent intent = new Intent();
                    intent.putExtra(Judo.JUDO_RECEIPT, receipt);
                    sendResult(Judo.RESULT_SUCCESS, intent);
                }, this::handleErrorCallback);
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
        super.onDestroy();
    }

    private void handleErrorCallback(final Throwable throwable) {
        if (throwable instanceof HttpException) {
            retrofit2.Response<?> response = ((HttpException) throwable).response();
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                Reader reader = errorBody.charStream();
                Gson gson = new Gson();
                Receipt receipt = gson.fromJson(reader, Receipt.class);
                Intent intent = new Intent();
                intent.putExtra(Judo.JUDO_RECEIPT, receipt);
                sendResult(Judo.RESULT_ERROR, intent);
            }
        } else if (throwable instanceof java.net.UnknownHostException) {
            sendResult(Judo.RESULT_CONNECTION_ERROR, new Intent());
        }
    }

    private void sendResult(final int resultCode, @NonNull final Intent intent) {
        setResult(resultCode, intent);
        finish();
    }
}
