package com.judopay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.judopay.arch.GooglePaymentUtils;
import com.judopay.arch.GooglePayIsReadyResult;
import com.judopay.model.PaymentMethod;
import com.judopay.view.SingleClickOnClickListener;

import java.util.EnumSet;

import static com.judopay.Judo.PAYMENT_REQUEST;

public class PaymentMethodFragment extends BaseFragment implements PaymentMethodView {

    private EnumSet<PaymentMethod> paymentMethod;
    private PaymentMethodPresenter presenter;

    private Button btnCardPayment;
    private View btnGPAY;

    public static Fragment newInstance(final Bundle extras) {
        Fragment fragment = new PaymentMethodFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paymentMethod = getJudo().getPaymentMethod();

        if (this.presenter == null) {
            this.presenter = new PaymentMethodPresenter(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        btnCardPayment = view.findViewById(R.id.btnCardPayment);
        btnGPAY = view.findViewById(R.id.btnGPAY);

        initializeCardPaymentButton();

        presenter.setPaymentMethod(paymentMethod);
    }

    private void initializeCardPaymentButton() {
        btnCardPayment.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
                startActivityForResult(intent, PAYMENT_REQUEST);
            }
        });
    }

    private void initializeGPAYButton(final Task<PaymentData> taskDefaultPaymentData) {
        btnGPAY.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                AutoResolveHelper.resolveTask(taskDefaultPaymentData, getActivity(), Judo.GPAY_REQUEST);
            }
        });
    }

    @Override
    public void displayPaymentMethodView(final int viewId) {
        View view = getView();
        if (view != null && view.findViewById(viewId) != null) {
            view.findViewById(viewId).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayAllPaymentMethods() {
        btnCardPayment.setVisibility(View.VISIBLE);
        setUpGPayButton();
    }

    @Override
    public void setUpGPayButton() {
        final PaymentsClient googlePayClient = GooglePaymentUtils.getGooglePayPaymentsClient(getContext(), getJudo().getEnvironmentModeGPay());

        GooglePaymentUtils.checkIsReadyGooglePay(googlePayClient, new GooglePayIsReadyResult() {
            @Override
            public void setResult(final boolean result) {
                btnGPAY.setVisibility(result ? View.VISIBLE : View.GONE);
                if (result) {
                    final PaymentDataRequest defaultPaymentData = GooglePaymentUtils.createDefaultPaymentDataRequest(getJudo());
                    final Task<PaymentData> taskDefaultPaymentData = googlePayClient.loadPaymentData(defaultPaymentData);
                    initializeGPAYButton(taskDefaultPaymentData);
                }
            }
        });
    }
}
