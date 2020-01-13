package com.judopay;

import com.judopay.model.Currency;
import com.judopay.model.OrderDetails;
import com.judopay.model.OrderStatus;
import com.judopay.model.SaleRequest;
import com.judopay.model.SaleResponse;
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

import static com.judopay.arch.TextUtil.isEmpty;

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
    private String consumerName;
    private int selectedBank;

    private JudoApiService apiService;
    private DateUtil dateUtil;
    private CompositeDisposable disposables = new CompositeDisposable();

    IdealPaymentPresenter(final IdealPaymentView view, final JudoApiService apiService, DateUtil dateUtil) {
        super(view);
        this.apiService = apiService;
        this.dateUtil = dateUtil;
    }

    void onCreate() {
        getView().setNameTextListener();
        getView().configureSpinner();
        getView().registerPayClickListener();
        getView().setMerchantTheme();
    }

    void setSelectedBank(int selectedBank) {
        this.selectedBank = selectedBank;
        handlePaymentButton();
    }

    void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
        handlePaymentButton();
    }

    void onPayClicked() {
        disposables.add(
                apiService.sale(buildSaleRequest(getView().getJudo(), getView().getName(), getView().getBank()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(saleResponse -> {
                                    this.saleResponse = saleResponse;
                                    getView().notifySaleResponse(saleResponse);
                                    getView().configureWebView(saleResponse.getRedirectUrl(), saleResponse.getMerchantRedirectUrl());
                                },
                                throwable -> getView().showGeneralError()));
    }

    void getTransactionStatus() {
        getView().showLoading();
        getView().hideStatus();
        getView().hideIdealPayment();
        disposables.add(
                apiService.status(saleResponse.getOrderId())
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
                            getView().hideLoading();
                            getView().showStatus(orderDetails.getOrderStatus());
                            getView().setCloseClickListener(response);

                        }, this::handleStatusError)
        );
    }

    @Override
    public void onPageStarted(String checksum) {
        getView().hideWebView();
        getTransactionStatus();
    }

    void dispose() {
        disposables.dispose();
    }

    private ObservableSource<? extends Long> handlePollingState() {
        long now = dateUtil.getDate().getTime();
        if (now >= halfIntervalFromNow) {
            getView().showDelayLabel();
        }
        statusRequestCounter++;
        return Observable.timer(DELAY, TimeUnit.SECONDS);
    }

    private void handleStatusError(Throwable throwable) {
        getView().hideLoading();
        if (throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException) {
            getView().showStatus(OrderStatus.NETWORK_ERROR);
            getView().setStatusClickListener();
        } else {
            getView().showStatus(OrderStatus.FAILED);
            getView().setOnFailClickListener(saleResponse.getOrderId());
        }
        statusRequestCounter = STATUS_FIRST_REQUEST;
    }

    private Boolean isTransactionCompleted(OrderStatus orderStatus) {
        return orderStatus == OrderStatus.SUCCEEDED || orderStatus == OrderStatus.FAILED || dateUtil.getDate().getTime() > fullIntervalFromNow;
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
                Currency.EUR,
                bic
        );
    }

    private void handlePaymentButton() {
        if (selectedBank == SPINNER_ITEM_PLACEHOLDER_POSITION || isEmpty(consumerName)) {
            getView().disablePayButton();
        } else {
            getView().enablePayButton();
        }
    }
}
