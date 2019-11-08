package com.judopay;

import com.judopay.model.OrderDetails;
import com.judopay.model.OrderStatus;
import com.judopay.model.SaleRequest;
import com.judopay.model.SaleResponse;
import com.judopay.model.SaleStatusRequest;
import com.judopay.model.SaleStatusResponse;
import com.judopay.util.DateUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdealPaymentPresenterTest {
    @Mock
    private IdealPaymentView view;
    @Mock
    private JudoApiService apiService;
    @Mock
    private DateUtil dateUtil;
    @InjectMocks
    private IdealPaymentPresenter presenter;
    @Mock
    private SaleRequest saleRequest;
    @Mock
    private SaleStatusRequest saleStatusRequest;
    @Mock
    private SaleResponse saleResponse;
    @Mock
    OrderDetails orderDetails;
    @Mock
    private SaleStatusResponse saleStatusResponse;
    private Calendar calendar = Calendar.getInstance();
    private final static String URL = "url";
    private final static int PLACEHOLDER_SELECTED = 0;
    private final static int FIRST_ITEM_SELECTED = 1;
    private final static Long THIRTY_SECONDS_PAST = 1573139806467L;
    private final static Long SIXTY_SECONDS_PAST = 1573139828474L;
    @Mock
    Date date;

    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        when(saleResponse.getRedirectUrl()).thenReturn(URL);
        when(apiService.sale(saleRequest)).thenReturn(Single.just(saleResponse));
        when(apiService.status(saleStatusRequest)).thenReturn(Observable.just(saleStatusResponse));
        when(dateUtil.getCalendar()).thenReturn(calendar);
        when(dateUtil.getDate()).thenReturn(date);
        when(dateUtil.getTimeWithInterval(calendar, 30, Calendar.SECOND)).thenReturn(THIRTY_SECONDS_PAST);
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
        presenter.handlePaymentButton(PLACEHOLDER_SELECTED);

        verify(view).disablePayButton();
    }

    @Test
    public void shouldEnablePaymentButtonOnSecondOrGreaterItemSelected() {
        presenter.handlePaymentButton(FIRST_ITEM_SELECTED);

        verify(view).enablePayButton();
    }

    @Test
    public void shouldConfigureWebViewOnSaleRequestSuccessful() {
        presenter.onPayClicked(saleRequest);

        verify(view).configureWebView(URL);
    }

    @Test
    public void shouldNotInitializeIdealPageOnSaleRequestUnsuccessful() {
        when(apiService.sale(saleRequest)).thenReturn(Single.error(new Exception()));
        presenter.onPayClicked(saleRequest);

        verify(view).showGeneralError();
    }

    @Test
    public void shouldShowLoadingScreenOnTransactionStatusRequest() {
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showLoading();
    }

    @Test
    public void shouldHideStatusScreenOnTransactionStatusRequest() {
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).hideStatus();
    }

    @Test
    public void shouldHideIdealPaymentScreenOnTransactionStatusRequest() {
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).hideIdealPayment();
    }

    @Test
    public void shouldNotShowDelayLabelOnTransactionStatusBeforeThirtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(0L);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view, never()).showDelayLabel();
    }

    @Test
    public void shouldShowDelayLabelOnTransactionStatusAfterThirtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(THIRTY_SECONDS_PAST);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showDelayLabel();
    }

    @Test
    public void shouldShowStatusButtonOnTransactionStatusAfterThirtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(THIRTY_SECONDS_PAST);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatusButton();
    }

    @Test
    public void shouldSetRetryClickListenerOnTransactionStatusAfterThirtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(THIRTY_SECONDS_PAST);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).setRetryClickListener();
    }

    @Test
    public void shouldHideStatusButtonOnRetryClick() {
        presenter.retryStatusRequest();

        verify(view).hideStatusButton();
    }

    @Test
    public void shouldHideDelayLabelOnRetryClick() {
        presenter.retryStatusRequest();

        verify(view).hideDelayLabel();
    }

    @Test
    public void shouldHideLoadingOnTransactionStatusAfterSixtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(SIXTY_SECONDS_PAST);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).hideLoading();
    }

    @Test
    public void shouldSetRetryClickListenerOnTransactionStatusAfterSixtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(SIXTY_SECONDS_PAST);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).setStatusClickListener(saleStatusRequest);
    }

    @Test
    public void shouldShowTimeoutStatusOnTransactionStatusAfterSixtySeconds() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(SIXTY_SECONDS_PAST);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatus(OrderStatus.TIMEOUT);
    }

    @Test
    public void shouldShowNetworkStatusOnTransactionStatusSocketTimeoutException() {
        when(apiService.status(saleStatusRequest)).thenReturn(Observable.error(new SocketTimeoutException()));
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatus(OrderStatus.NETWORK_ERROR);
    }

    @Test
    public void shouldShowNetworkStatusOnTransactionStatusUnknownHostException() {
        when(apiService.status(saleStatusRequest)).thenReturn(Observable.error(new UnknownHostException()));
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatus(OrderStatus.NETWORK_ERROR);
    }

    @Test
    public void shouldShowFailStatusOnTransactionStatusRequestFail() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.FAIL);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatus(OrderStatus.FAIL);
    }

    @Test
    public void shouldHideLoadingOnTransactionStatusSuccess() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.SUCCESS);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).hideLoading();
    }

    @Test
    public void shouldShowSuccessStatusScreenOnTransactionStatusSuccess() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.SUCCESS);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatus(OrderStatus.SUCCESS);
    }

    @Test
    public void shouldSetCloseClickListenerOnTransactionStatusSuccess() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(orderDetails.getOrderStatus()).thenReturn(OrderStatus.SUCCESS);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showStatus(OrderStatus.SUCCESS);
    }

    @Test
    public void shouldHideWebViewOnChecksumCaptured() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        presenter.onPageStarted(saleStatusRequest);

        verify(view).hideWebView();
    }
}