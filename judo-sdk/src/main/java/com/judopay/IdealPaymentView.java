package com.judopay;

import com.judopay.model.OrderStatus;
import com.judopay.model.SaleResponse;
import com.judopay.model.SaleStatusRequest;
import com.judopay.model.SaleStatusResponse;

interface IdealPaymentView extends BaseView {
    void registerPayClickListener();

    void configureWebView(String url, String merchantRedirectUrl);

    void configureSpinner();

    void enablePayButton();

    void disablePayButton();

    void showStatus(OrderStatus orderStatus);

    void showLoading();

    void hideStatus();

    void hideIdealPayment();

    void hideLoading();

    void setStatusClickListener(SaleStatusRequest saleStatusRequest);

    void setCloseClickListener(SaleStatusResponse saleStatusResponse);

    void showDelayLabel();

    void showGeneralError();

    void hideWebView();

    Judo getJudo();

    String getName();

    String getBank();

    void notifySaleResponse(SaleResponse saleResponse);
}
