package com.judopay;

import com.judopay.model.OrderDetails;
import com.judopay.model.OrderStatus;
import com.judopay.model.SaleRequest;
import com.judopay.model.SaleResponse;
import com.judopay.model.SaleStatusRequest;
import com.judopay.util.DateUtil;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

class IdealPaymentPresenter extends BasePresenter<IdealPaymentView> implements IdealWebViewCallback {
    private final static int SPINNER_ITEM_PLACEHOLDER_POSITION = 0;
    private final static int STATUS_FIRST_REQUEST = 1;
    private final static int DELAY = 5;
    private final static String IDEAL_PAYMENT_METHOD = "IDEAL";
    private final static String COUNTRY = "NL";

    private SaleResponse saleResponse;
    private int statusRequestCounter = STATUS_FIRST_REQUEST;
    private Long halfIntervalFromNow;
    private Long fullIntervalFromNow;

    private JudoApiService apiService;
    private DateUtil dateUtil;
    private CompositeDisposable disposables = new CompositeDisposable();

    IdealPaymentPresenter(final IdealPaymentView view, final JudoApiService apiService, DateUtil dateUtil) {
        super(view);
        this.apiService = apiService;
        this.dateUtil = dateUtil;
    }

    void onCreate() {
        getView().configureSpinner();
        getView().registerPayClickListener();
    }

    void handlePaymentButton(final int position) {
        if (position == SPINNER_ITEM_PLACEHOLDER_POSITION) {
            getView().disablePayButton();
        } else {
            getView().enablePayButton();
        }
    }

    void onPayClicked() {
        disposables.add(
                apiService.sale(buildSaleRequest(getView().getJudo(), getView().getName(), getView().getBank()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(saleResponse -> {
                                    this.saleResponse = saleResponse;
                                    getView().configureWebView(saleResponse.getRedirectUrl(), saleResponse.getMerchantRedirectUrl());
                                },
                                throwable -> getView().showGeneralError()));
    }

    void getTransactionStatus(SaleStatusRequest saleStatusRequest) {
        getView().showLoading();
        getView().hideStatus();
        getView().hideIdealPayment();
        disposables.add(
                apiService.status(saleStatusRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .repeatWhen(observable -> observable.flatMap(aVoid -> handlePollingState()))
                        .takeUntil(response -> {
                            OrderStatus orderStatus = response.getOrderDetails().getOrderStatus();
                            return isTransactionCompleted(orderStatus);
                        })
                        .filter(response -> {
                            setDateInterval();
                            OrderStatus orderStatus = response.getOrderDetails().getOrderStatus();
                            return isTransactionCompleted(orderStatus);
                        })
                        .subscribe(response -> {
                            OrderDetails orderDetails = response.getOrderDetails();
                            switch (orderDetails.getOrderStatus()) {
                                case SUCCESS:
                                case FAIL:
                                    getView().hideLoading();
                                    getView().showStatus(orderDetails.getOrderStatus());
                                    getView().setCloseClickListener(orderDetails.getOrderId(), orderDetails.getOrderStatus());
                                    break;
                            }
                        }, throwable -> handleStatusError(saleStatusRequest, throwable))
        );
    }

    @Override
    public void onPageStarted(String checksum) {
        getView().hideWebView();
        SaleStatusRequest saleStatusRequest = buildSaleStatusRequest(getView().getJudo(), checksum);
        getTransactionStatus(saleStatusRequest);
    }

    void dispose() {
        disposables.dispose();
    }

    private ObservableSource<? extends Long> handlePollingState() {
        long now = dateUtil.getDate().getTime();
        if (now >= halfIntervalFromNow) {
            getView().showDelayLabel();
        }
        if (now >= fullIntervalFromNow) {
            throw new PollingTimeoutException();
        }
        statusRequestCounter++;
        return Observable.timer(DELAY, TimeUnit.SECONDS);
    }

    private void handleStatusError(SaleStatusRequest saleStatusRequest, Throwable throwable) {
        getView().hideLoading();
        if (throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException) {
            getView().showStatus(OrderStatus.NETWORK_ERROR);
            getView().setStatusClickListener(saleStatusRequest);
        } else if (throwable instanceof PollingTimeoutException) {
            getView().showStatus(OrderStatus.TIMEOUT);
            getView().setCloseClickListener(saleStatusRequest.getOrderId(), OrderStatus.TIMEOUT);
        } else {
            getView().showStatus(OrderStatus.FAIL);
            getView().setCloseClickListener(saleStatusRequest.getOrderId(), OrderStatus.FAIL);
        }
        statusRequestCounter = STATUS_FIRST_REQUEST;
    }

    private Boolean isTransactionCompleted(OrderStatus orderStatus) {
        return orderStatus == OrderStatus.SUCCESS || orderStatus == OrderStatus.FAIL;
    }

    private void setDateInterval() {
        if (statusRequestCounter == STATUS_FIRST_REQUEST) {
            Calendar calendar = dateUtil.getCalendar();
            halfIntervalFromNow = dateUtil.getTimeWithInterval(calendar, getView().getJudo().getIdealTimeout() / 2, Calendar.SECOND);
            fullIntervalFromNow = dateUtil.getTimeWithInterval(calendar, getView().getJudo().getIdealTimeout() / 2, Calendar.SECOND);
        }
    }

    SaleRequest buildSaleRequest(Judo judo, String name, String bic) {
        return new SaleRequest(
                COUNTRY,
                new BigDecimal(judo.getAmount()),
                judo.getPaymentReference(),
                judo.getMetaDataMap(),
                judo.getConsumerReference(),
                name,
                IDEAL_PAYMENT_METHOD,
                judo.getJudoId(),
                judo.getCurrency(),
                bic
        );
    }

    SaleStatusRequest buildSaleStatusRequest(Judo judo, String checksum) {
        return new SaleStatusRequest(
                saleResponse.getOrderId(),
                saleResponse.getMerchantPaymentReference(),
                checksum,
                IDEAL_PAYMENT_METHOD,
                judo.getJudoId(),
                judo.getMetaDataMap(),
                judo.getConsumerReference()
        );
    }
}
