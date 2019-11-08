package com.judopay;

import com.judopay.model.SaleStatusRequest;

interface IdealWebViewCallback {
    void onPageStarted(SaleStatusRequest saleStatusRequest);
}
