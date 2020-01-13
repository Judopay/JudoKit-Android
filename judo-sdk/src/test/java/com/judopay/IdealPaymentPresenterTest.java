package com.judopay;

import com.judopay.model.OrderDetails;
import com.judopay.model.OrderStatus;
import com.judopay.model.SaleRequest;
import com.judopay.model.SaleResponse;
import com.judopay.model.SaleStatusResponse;
import com.judopay.util.DateUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdealPaymentPresenterTest {
    private IdealPaymentPresenter presenter;
    @Mock
    private IdealPaymentView view;
    @Mock
    private JudoApiService apiService;
    @Mock
    private DateUtil dateUtil;
    @Mock
    private SaleRequest saleRequest;
    @Mock
    private SaleResponse saleResponse;
    @Mock
    OrderDetails orderDetails;
    @Mock
    private SaleStatusResponse saleStatusResponse;
    @Mock
    private Calendar calendar;
    private final static String URL = "url";
    private final static String CHECKSUM = "checksum";
    private final static String NAME = "name";
    private final static String BIC = "bic";
    private final static int PLACEHOLDER_SELECTED = 0;
    private final static int FIRST_ITEM_SELECTED = 1;
    private final static int INTERVAL = 1;
    private final static Long HALF_INTERVAL = 1584948186919L;
    private final static Long FULL_INTERVAL = 1573139828474L;
    @Mock
    Date date;
    @Mock
    Judo judo;

    @Before
    public void setUp() {
        presenter = spy(new IdealPaymentPresenter(view, apiService, dateUtil));
        doReturn(saleRequest).when(presenter).buildSaleRequest(judo, NAME, BIC);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        when(apiService.status(saleResponse.getOrderId())).thenReturn(Observable.just(saleStatusResponse));
        when(apiService.sale(saleRequest)).thenReturn(Single.just(saleResponse));
        when(dateUtil.getCalendar()).thenReturn(calendar);
        when(dateUtil.getDate()).thenReturn(date);
        when(view.getJudo()).thenReturn(judo);
        when(view.getName()).thenReturn(NAME);
        when(view.getBank()).thenReturn(BIC);
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(saleResponse.getRedirectUrl()).thenReturn(URL);
        when(saleResponse.getOrderId()).thenReturn(null);
    }

    @Test
    public void shouldConfigureSpinnerOnCreate() {
        presenter.onCreate();

        verify(view).configureSpinner();
    }

    @Test
    public void shouldRegisterPayClickListenerOnCreate() {
        presenter.onCreate();

        verify(view).registerPayClickListener();
    }

    @Test
    public void shouldDisablePaymentButtonOnFirstItemSelected() {
        presenter.setSelectedBank(PLACEHOLDER_SELECTED);

        verify(view).disablePayButton();
    }

    @Test
    public void shouldDisablePaymentButtonOnNameIsEmpty() {
        presenter.setConsumerName("");

        verify(view).disablePayButton();
    }

    @Test
    public void shouldEnablePaymentButtonOnBankSelectedAndNameNotEmpty() {
        presenter.setSelectedBank(FIRST_ITEM_SELECTED);
        presenter.setConsumerName("Name");

        verify(view).disablePayButton();
    }

    @Test
    public void shouldConfigureWebViewOnSaleRequestSuccessful() {
        presenter.onPayClicked();

        verify(view).configureWebView(URL, saleResponse.getMerchantRedirectUrl());
    }

    @Test
    public void shouldNotInitializeIdealPageOnSaleRequestUnsuccessful() {
        when(apiService.sale(saleRequest)).thenReturn(Single.error(new Exception()));
        presenter.onPayClicked();

        verify(view).showGeneralError();
    }

    @Test
    public void shouldShowLoadingScreenOnTransactionStatusRequest() {
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showLoading();
    }

    @Test
    public void shouldHideStatusScreenOnTransactionStatusRequest() {
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).hideStatus();
    }

    @Test
    public void shouldHideIdealPaymentScreenOnTransactionStatusRequest() {
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).hideIdealPayment();
    }

    @Test
    public void shouldShowDelayLabelOnTransactionStatusAfterHalfOfInterval() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(judo.getIdealTimeout()).thenReturn(INTERVAL);
        when(dateUtil.getDate().getTime()).thenReturn(HALF_INTERVAL);
        when(dateUtil.getTimeWithInterval(calendar, INTERVAL / 2, Calendar.SECOND)).thenReturn(HALF_INTERVAL);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showDelayLabel();
    }

    @Test
    public void shouldHideLoadingOnTransactionStatusAfterFullInterval() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(FULL_INTERVAL);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).hideLoading();
    }

    @Test
    public void shouldShowTimeoutStatusOnTransactionStatusAfterFullInterval() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.TIMEOUT);
        when(date.getTime()).thenReturn(FULL_INTERVAL);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.TIMEOUT);
    }

    @Test
    public void shouldShowNetworkStatusOnTransactionStatusSocketTimeoutException() {
        when(apiService.status(saleResponse.getOrderId())).thenReturn(Observable.error(new SocketTimeoutException()));
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.NETWORK_ERROR);
    }

    @Test
    public void shouldShowNetworkStatusOnTransactionStatusUnknownHostException() {
        when(apiService.status(saleResponse.getOrderId())).thenReturn(Observable.error(new UnknownHostException()));
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.NETWORK_ERROR);
    }

    @Test
    public void shouldShowFailStatusOnTransactionStatusException() {
        when(apiService.status(saleResponse.getOrderId())).thenReturn(Observable.error(new Exception()));
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.FAILED);
    }

    @Test
    public void shouldShowFailStatusOnTransactionStatusRequestFail() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.FAILED);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.FAILED);
    }

    @Test
    public void shouldHideLoadingOnTransactionStatusSuccess() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.SUCCEEDED);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).hideLoading();
    }

    @Test
    public void shouldShowSuccessStatusScreenOnTransactionStatusSuccess() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.SUCCEEDED);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.SUCCEEDED);
    }

    @Test
    public void shouldSetCloseClickListenerOnTransactionStatusSuccess() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.SUCCEEDED);
        presenter.onPayClicked();
        presenter.getTransactionStatus();

        verify(view).showStatus(OrderStatus.SUCCEEDED);
    }

    @Test
    public void shouldHideWebViewOnChecksumCaptured() {
        presenter.onPayClicked();
        presenter.onPageStarted(CHECKSUM);

        verify(view).hideWebView();
    }
}