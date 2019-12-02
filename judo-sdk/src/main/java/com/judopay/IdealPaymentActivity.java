package com.judopay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.judopay.model.Bank;
import com.judopay.model.OrderStatus;
import com.judopay.model.SaleCallback;
import com.judopay.model.SaleResponse;
import com.judopay.model.SaleStatusRequest;
import com.judopay.util.DefaultDateUtil;

import static com.judopay.Judo.BR_SALE_CALLBACK;
import static com.judopay.Judo.IDEAL_ORDER_ID;
import static com.judopay.Judo.IDEAL_PAYMENT;
import static com.judopay.Judo.IDEAL_STATUS;

public class IdealPaymentActivity extends BaseActivity implements IdealPaymentView {
    private final static String IDEAL_HOST = "https://api.judopay.com/";

    private IdealPaymentPresenter presenter;

    TextInputEditText nameEditText;
    Button payWithIdealButton;
    Button statusButton;
    Spinner bankSpinner;
    ConstraintLayout statusViewLayout;
    ConstraintLayout idealPaymentLayout;
    TextView statusTextView;
    ImageView statusImageView;
    ProgressBar statusProgressBar;
    private WebView idealWebView;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.ideal);
        setContentView(R.layout.activity_ideal_payment);

        nameEditText = findViewById(R.id.name_edit_text);
        payWithIdealButton = findViewById(R.id.ideal_payment_button);
        bankSpinner = findViewById(R.id.bank_spinner);
        idealPaymentLayout = findViewById(R.id.ideal_payment_layout);
        statusViewLayout = findViewById(R.id.status_view_layout);
        statusTextView = findViewById(R.id.status_text_view);
        statusImageView = findViewById(R.id.status_image_view);
        statusProgressBar = findViewById(R.id.status_progress_bar);
        statusButton = findViewById(R.id.status_button);
        idealWebView = findViewById(R.id.ideal_web_view);

        getJudo().setEnvironmentHost(IDEAL_HOST);
        JudoApiService apiService = getJudo().getApiService(this, Judo.UI_CLIENT_MODE_JUDO_SDK);
        presenter = new IdealPaymentPresenter(this, apiService, new DefaultDateUtil());
        presenter.onCreate();
    }

    @Override
    public void registerPayClickListener() {
        payWithIdealButton.setOnClickListener(view -> {
            presenter.onPayClicked();
            closeKeyboard(view);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void configureWebView(String url, String merchantRedirectUrl) {
        idealWebView.setVisibility(View.VISIBLE);
        idealWebView.setWebViewClient(new IdealWebViewClient(presenter, merchantRedirectUrl));
        idealWebView.getSettings().setJavaScriptEnabled(true);
        idealWebView.loadUrl(url);
    }

    @Override
    public void configureSpinner() {
        BankAdapter adapter = new BankAdapter(this, Bank.values());
        bankSpinner.setAdapter(adapter);
        bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int position, final long id) {
                presenter.handlePaymentButton(position);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        bankSpinner.setOnTouchListener((view, motionEvent) -> {
            nameEditText.clearFocus();
            closeKeyboard(view);
            return view.performClick();
        });
    }

    @Override
    public void enablePayButton() {
        payWithIdealButton.setEnabled(true);
    }

    @Override
    public void disablePayButton() {
        payWithIdealButton.setEnabled(false);
    }

    @Override
    public void showStatus(OrderStatus orderStatus) {
        statusButton.setVisibility(View.VISIBLE);
        statusImageView.setVisibility(View.VISIBLE);
        statusTextView.setText(orderStatus.getOrderStatusTextId());
        statusButton.setText(orderStatus.getOrderStatusButtonTextId());
        statusImageView.setImageResource(orderStatus.getOrderStatusImageId());
    }

    @Override
    public void showLoading() {
        statusTextView.setText(R.string.processing_payment);
        statusViewLayout.setVisibility(View.VISIBLE);
        statusProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        statusProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setStatusClickListener(SaleStatusRequest saleStatusRequest) {
        statusButton.setOnClickListener(view -> presenter.getTransactionStatus(saleStatusRequest));
    }

    @Override
    public void setCloseClickListener(String orderId, OrderStatus orderStatus) {
        statusButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(IDEAL_ORDER_ID, orderId);
            intent.putExtra(IDEAL_STATUS, orderStatus);
            switch (orderStatus) {
                case SUCCESS:
                    setResult(Judo.IDEAL_SUCCESS, intent);
                case FAIL:
                    setResult(Judo.IDEAL_ERROR, intent);
                default:
                    //noop
            }
            finish();
        });
    }

    @Override
    public void showDelayLabel() {
        statusTextView.setText(R.string.there_is_a_delay);
    }

    @Override
    public void showGeneralError() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void hideWebView() {
        idealWebView.setVisibility(View.GONE);
    }

    @Override
    public Judo getJudo() {
        return super.getJudo();
    }

    @Override
    public String getName() {
        return nameEditText.getText().toString();
    }

    @Override
    public String getBank() {
        Bank bank = (Bank) bankSpinner.getSelectedItem();
        return getString(bank.getBicId());
    }

    @Override
    public void notifySaleResponse(SaleResponse saleResponse) {
        Intent intent = new Intent(BR_SALE_CALLBACK);
        intent.putExtra(BR_SALE_CALLBACK, new SaleCallback(saleResponse, getJudo().getPaymentReference()));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void hideStatus() {
        statusImageView.setVisibility(View.GONE);
        statusButton.setVisibility(View.GONE);
    }

    @Override
    public void hideIdealPayment() {
        idealPaymentLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        presenter.dispose();
        super.onDestroy();
    }

    private void closeKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void openIdealScreen(final Activity activity, final Judo judo) {
        Intent intent = new Intent(activity, IdealPaymentActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, judo);
        activity.startActivityForResult(intent, IDEAL_PAYMENT);
    }
}
