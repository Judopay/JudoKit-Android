package com.judopay;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.judopay.arch.GooglePayIsReadyResult;
import com.judopay.arch.GooglePaymentUtils;
import com.judopay.model.Match;
import com.judopay.model.PaymentMethod;
import com.judopay.view.SingleClickOnClickListener;

import java.util.Base64;
import java.util.EnumSet;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PaymentMethodFragment extends BaseFragment implements PaymentMethodView {

    private EnumSet<PaymentMethod> paymentMethod;
    private PaymentMethodPresenter presenter;

    private Button btnCardPayment;
    private LinearLayout btnIdealPayment;
    private View btnGPAY;

    public static Fragment newInstance(final Bundle extras) {
        Fragment fragment = new PaymentMethodFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paymentMethod = getJudo().getPaymentMethod();

        if (this.presenter == null) {
            this.presenter = new PaymentMethodPresenter(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        btnCardPayment = view.findViewById(R.id.btnCardPayment);
        btnGPAY = view.findViewById(R.id.btnGPAY);
        btnIdealPayment = view.findViewById(R.id.ideal_payment_button);

        initializeCardPaymentButton();

        presenter.setPaymentMethod(paymentMethod);
        presenter.setIdealPaymentMethod(getJudo().isIdealEnabled());
    }

    private void initializeCardPaymentButton() {
        btnCardPayment.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
//                Intent intent = new Intent(getContext(), PaymentActivity.class);
//                intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
//                startActivityForResult(intent, PAYMENT_REQUEST);
                JudoApiService apiService = getJudo().getApiService(getActivity(), Judo.UI_CLIENT_MODE_JUDO_SDK);
                String evurlEncryptionKey = "jpcvSrx314vCWlR84hgdng==";
                String cipherSalt = "1234567891234567";

                String clientAccessKey = "xM8sWMFO";
                String correlationId = "correlationid=1422740606035IA";
                String timestamp = "&timestamp=20191106051859";
                String nonce = "&nonce=10112";
                Match match = new Match("+14085551234");
//                String matchJson = "&match=" + new Gson().toJson(match);
                String dataPlain = correlationId + timestamp + nonce;
                System.out.println(dataPlain);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    System.out.println("ININ");
                    String dataEncoded = encrypt(dataPlain, evurlEncryptionKey, cipherSalt);
                    System.out.println(dataEncoded + " dataencoded");
                    System.out.println(decryptPayload(evurlEncryptionKey, dataEncoded, "AES/CTR/NoPadding", cipherSalt));

                    apiService.phoneVerification(clientAccessKey, "data=" + dataEncoded + "&cipherSalt=" + cipherSalt)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(o -> {
                                System.out.println(o);
                            }, throwable -> System.out.println(throwable.getMessage()));
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String strToEncrypt, String secret, String cipherSalt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret.getBytes(), "AES"), new IvParameterSpec(cipherSalt.getBytes()));
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
//            return Base64.getUrlEncoder().encodeToString(encodedData);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptPayload(String aesDecryptionKey, String encryptedData, String encryptionType, String cipherSalt) {
        System.out.println("aesDecryptionKey:" + aesDecryptionKey + ",   encryptedData: " + encryptedData + ", encryptionType: " + encryptionType + ",cipherSalt: " + cipherSalt);

        try {
//            byte[] decodedSecretKey = Base64.getDecoder().decode(aesDecryptionKey);
            byte[] decodedPayload = Base64.getDecoder().decode(encryptedData.trim());
            IvParameterSpec iv = null;
            Cipher cipher = Cipher.getInstance(encryptionType);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesDecryptionKey.getBytes(), "AES");
            if (cipherSalt != null) {
                iv = new IvParameterSpec(cipherSalt.getBytes("UTF-8"));
            } else {
                System.out.println(" SALT MISSING. SKIPPING IV creation ENCRYPTION ALGO is: " + encryptionType);
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] decryptedDataBytes = cipher.doFinal(decodedPayload);
            String decryptedData = new String(decryptedDataBytes);
            return decryptedData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void initializeGPAYButton(final Task<PaymentData> taskDefaultPaymentData) {
        btnGPAY.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                AutoResolveHelper.resolveTask(taskDefaultPaymentData, getActivity(), Judo.GPAY_REQUEST);
            }
        });
    }

    @Override
    public void displayPaymentMethodView(final int viewId) {
        View view = getView();
        if (view != null && view.findViewById(viewId) != null) {
            view.findViewById(viewId).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayAllPaymentMethods() {
        btnCardPayment.setVisibility(View.VISIBLE);
        setUpGPayButton();
    }

    @Override
    public void setUpGPayButton() {
        final PaymentsClient googlePayClient = GooglePaymentUtils.getGooglePayPaymentsClient(getContext(), getJudo().getEnvironmentModeGPay());

        GooglePaymentUtils.checkIsReadyGooglePay(googlePayClient, new GooglePayIsReadyResult() {
            @Override
            public void setResult(final boolean result) {
                btnGPAY.setVisibility(result ? View.VISIBLE : View.GONE);
                if (result) {
                    final PaymentDataRequest defaultPaymentData = GooglePaymentUtils.createDefaultPaymentDataRequest(getJudo());
                    final Task<PaymentData> taskDefaultPaymentData = googlePayClient.loadPaymentData(defaultPaymentData);
                    initializeGPAYButton(taskDefaultPaymentData);
                }
            }
        });
    }

    @Override
    public void showIdealButton() {
        btnIdealPayment.setVisibility(View.VISIBLE);
    }

    @Override
    public void setIdealPaymentClickListener() {
        btnIdealPayment.setOnClickListener(new SingleClickOnClickListener() {
            @Override
            public void doClick() {
                IdealPaymentActivity.openIdealScreen(getActivity(), getJudo());
            }
        });
    }
}
