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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
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
    private SaleStatusRequest saleStatusRequest;
    @Mock
    private SaleResponse saleResponse;
    @Mock
    OrderDetails orderDetails;
    @Mock
    private SaleStatusResponse saleStatusResponse;
    private Calendar calendar = Calendar.getInstance();
    private final static String URL = "url";
    private final static String CHECKSUM = "checksum";
    private final static String NAME = "name";
    private final static String BIC = "bic";
    private final static int PLACEHOLDER_SELECTED = 0;
    private final static int FIRST_ITEM_SELECTED = 1;
    private final static Long HALF_INTERVAL = 1573139806467L;
    private final static Long FULL_INTERVAL = 1573139828474L;
    @Mock
    Date date;
    @Mock
    Judo judo;
    @Before
    public void setUp() {
        presenter = spy(new IdealPaymentPresenter(view, apiService, dateUtil));
        doReturn(saleRequest).when(presenter).buildSaleRequest(judo, NAME, BIC);
        doReturn(saleStatusRequest).when(presenter).buildSaleStatusRequest(judo, CHECKSUM);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        when(apiService.status(saleStatusRequest)).thenReturn(Observable.just(saleStatusResponse));
        when(apiService.sale(saleRequest)).thenReturn(Single.just(saleResponse));
        when(dateUtil.getCalendar()).thenReturn(calendar);
        when(dateUtil.getDate()).thenReturn(date);
        when(view.getJudo()).thenReturn(judo);
        when(view.getName()).thenReturn(NAME);
        when(view.getBank()).thenReturn(BIC);
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(saleResponse.getRedirectUrl()).thenReturn(URL);
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
    public void shouldConfigureWebViewOnSaleRequestSuccessful() {
        presenter.onPayClicked();

        verify(view).configureWebView(URL);
    }

    @Test
    public void shouldNotInitializeIdealPageOnSaleRequestUnsuccessful() {
        when(apiService.sale(saleRequest)).thenReturn(Single.error(new Exception()));
        presenter.onPayClicked();

        verify(view).showGeneralError();
    }

    @Test
    public void shouldEnablePaymentButtonOnSecondOrGreaterItemSelected() {
        presenter.handlePaymentButton(FIRST_ITEM_SELECTED);

        verify(view).enablePayButton();
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
    public void shouldShowDelayLabelOnTransactionStatusAfterHalfOfInterval() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(HALF_INTERVAL);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).showDelayLabel();
    }

    @Test
    public void shouldHideLoadingOnTransactionStatusAfterFullInterval() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(FULL_INTERVAL);
        presenter.getTransactionStatus(saleStatusRequest);

        verify(view).hideLoading();
    }

    @Test
    public void shouldShowTimeoutStatusOnTransactionStatusAfterFullInterval() {
        when(saleStatusResponse.getOrderDetails()).thenReturn(orderDetails);
        when(date.getTime()).thenReturn(FULL_INTERVAL);
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
        presenter.onPayClicked();
        presenter.onPageStarted(CHECKSUM);

        verify(view).hideWebView();
    }
}