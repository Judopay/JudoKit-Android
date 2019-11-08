package com.judopay;

import com.judopay.model.OrderStatus;
import com.judopay.model.SaleStatusRequest;

interface IdealPaymentView extends BaseView {
    void registerPayClickListener();

    void configureWebView(String url);

    void configureSpinner();

    void enablePayButton();

    void disablePayButton();

    void showStatus(OrderStatus orderStatus);

    void showLoading();

    void hideStatus();

    void hideIdealPayment();

    void hideLoading();

    void setStatusClickListener(SaleStatusRequest saleStatusRequest);

    void setCloseClickListener(String orderId, OrderStatus orderStatus);

    void showDelayLabel();

    void showGeneralError();

    void showStatusButton();

    void setRetryClickListener();

    void hideStatusButton();

    void hideDelayLabel();

    void hideWebView();
}
