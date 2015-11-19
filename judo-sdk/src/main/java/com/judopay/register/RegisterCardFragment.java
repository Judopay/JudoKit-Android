package com.judopay.register;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.payment.Receipt;
import com.judopay.payment.form.PaymentFormFragment;

public class RegisterCardFragment extends Fragment implements PaymentFormView {

    public static final String KEY_CONSUMER = "Judo-Consumer";

    private RegisterCardPresenter presenter;
    private View progressOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (savedInstanceState == null) {
            Consumer consumer = getArguments().getParcelable(KEY_CONSUMER);

            this.presenter = new RegisterCardPresenter(consumer, this, RetrofitFactory.getInstance().create(JudoApiService.class));

            PaymentFormFragment paymentFormFragment = PaymentFormFragment.newInstance(this.presenter, getString(R.string.add_card));
            paymentFormFragment.setRetainInstance(true);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, paymentFormFragment)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressOverlay = view.findViewById(R.id.progress_overlay);

        this.presenter.reconnect();
    }

    @Override
    public void showLoading() {
        progressOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressOverlay.setVisibility(View.GONE);
    }

    @Override
    public void finish(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(JudoPay.RESULT_REGISTER_CARD_SUCCESS, intent);
            activity.finish();
        }
    }

    @Override
    public void showDeclinedMessage(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(JudoPay.RESULT_REGISTER_CARD_DECLINED, intent);
            activity.finish();
        }
    }

}