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

import com.judopay.model.PaymentMethod;
import com.judopay.view.SingleClickOnClickListener;
import com.zapp.library.merchant.ui.view.PBBAButton;

import java.util.EnumSet;

import static com.judopay.Judo.PAYMENT_REQUEST;

public class PaymentMethodFragment extends BaseFragment implements PaymentMethodView {

    private EnumSet<PaymentMethod> paymentMethod;
    private PaymentMethodPresenter presenter;

    private Button btnCardPayment;
    private PBBAButton btnPBBA;
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
        btnPBBA = view.findViewById(R.id.btnPBBA);
        btnGPAY = view.findViewById(R.id.btnGPAY);

        initializeCardPaymentButton();
        initializePBBAButton();
        initializeGPAYButton();

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

    private void initializePBBAButton() {
        btnPBBA.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                //TODO start PBBA flow
            }
        });
    }

    private void initializeGPAYButton() {
        btnGPAY.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                //TODO start GPAY flow
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
        btnPBBA.setVisibility(View.VISIBLE);
        btnGPAY.setVisibility(View.VISIBLE);
    }
}
