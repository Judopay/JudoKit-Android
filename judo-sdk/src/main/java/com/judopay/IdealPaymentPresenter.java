package com.judopay;

import com.judopay.model.OrderDetails;
import com.judopay.model.OrderStatus;
import com.judopay.model.SaleRequest;
import com.judopay.model.SaleStatusRequest;
import com.judopay.util.DateUtil;

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
    private final static int INTERVAL = 30;

    private int statusRequestCounter = STATUS_FIRST_REQUEST;
    private Long thirtySecondsFromNow;
    private Long oneMinuteFromNow;

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

    void onPayClicked(SaleRequest saleRequest) {
        disposables.add(
                apiService.sale(saleRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(saleResponse -> getView().configureWebView(saleResponse.getRedirectUrl()),
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

    void retryStatusRequest() {
        this.statusRequestCounter = STATUS_FIRST_REQUEST;
        getView().hideStatusButton();
        getView().hideDelayLabel();
    }

    @Override
    public void onPageStarted(SaleStatusRequest saleStatusRequest) {
        getView().hideWebView();
        getTransactionStatus(saleStatusRequest);
    }

    void dispose() {
        disposables.dispose();
    }

    private ObservableSource<? extends Long> handlePollingState() {
        long now = dateUtil.getDate().getTime();
        if (now >= thirtySecondsFromNow) {
            getView().showDelayLabel();
            getView().showStatusButton();
            getView().setRetryClickListener();
        }
        if (now >= oneMinuteFromNow) {
            throw new RuntimeException();
        }
        statusRequestCounter++;
        return Observable.timer(DELAY, TimeUnit.SECONDS);
    }

    private void handleStatusError(SaleStatusRequest saleStatusRequest, Throwable throwable) {
        getView().hideLoading();
        getView().setStatusClickListener(saleStatusRequest);
        if (throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException) {
            getView().showStatus(OrderStatus.NETWORK_ERROR);
        } else {
            getView().showStatus(OrderStatus.TIMEOUT);
        }
        statusRequestCounter = STATUS_FIRST_REQUEST;
    }

    private Boolean isTransactionCompleted(OrderStatus orderStatus) {
        return orderStatus == OrderStatus.SUCCESS || orderStatus == OrderStatus.FAIL;
    }

    private void setDateInterval() {
        if (statusRequestCounter == STATUS_FIRST_REQUEST) {
            Calendar calendar = dateUtil.getCalendar();
            thirtySecondsFromNow = dateUtil.getTimeWithInterval(calendar, INTERVAL, Calendar.SECOND);
            oneMinuteFromNow = dateUtil.getTimeWithInterval(calendar, INTERVAL, Calendar.SECOND);
        }
    }
}
