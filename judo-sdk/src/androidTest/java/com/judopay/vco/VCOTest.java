package com.judopay.vco;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.VCOPaymentRequest;
import com.judopay.model.VCOWallet;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.observers.TestObserver;

import static com.judopay.TestUtil.JUDO_ID_IRIDIUM;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class VCOTest {
    private static final String CALL_ID = "3033741658316443501";
    private static final String ENC_KEY = "h1cBYhr/THQ8DpteW8QZBJQQJ0VTUHoW1reGZt03b09TkOp0dMiq+2y6SeODqoiok/I3KbwdV/6iojlLU4csDQW/jF+RpG/8XerY2cW7CYvXU+YmEEjH9pzLySb+yVLk";
    private static final String ENC_PAYMENT_DATA = "wTO7a12G1g17Cg8add4fCZhZLoUT/FNqwn9DpufWRkOLOPmm3n/EZlyv8vC7MepQRPw3Z7HtH6ZgyMlV+BgwyoNgdXISjpVGyuLpGnpiiCyb7TuVncoVCYvjuPlnfjuYfGp8swH92xrI8lu6Whaq65ecneXtejHOlaTJAjZ+AnhqjZWI2d89tRcRJpdh8wWWemqtus1tGoE/qaUa+8xxfI+qwGHGSkFbvZopAcGS17fVHgqQcgeAU1vaCaGCXLZvr1mbGOWAVMtfVi7/QWz9/Er+/WV9BPh09Ge6SE4KUVgMs9wZD22jA8NM1U7diNM64RCrgcBD9Eli8YQSUrzty6/KkA4pzRX015SfeMblP8sXj8Yuw12FA5JpZo7jEjmZhiwxZs/WrQMvagj9Y6q/N+29DfPW/nTdwFzS0Hu+PU2xgZIg8cGdl8vxnnR8rlg/71BWGvIYgDqwsUpm6qPJ7KVK2RkKCCy9ANuVii/gR+u/aiOa5Cj6Caq/M86Xct5RBrqaiQubPFsIzQDVsOOrvf6I8uLtTDb0sTeOtFlfpoZhZCYw+PkGhTxPciXb6DBu9NUFWJkRzmwBlQlI9VMS7blN/AEQim7dY3MA75S4wi8Irv5r9OjKPXgA+Mo7JCtjIXbQUvCSHL8++h6NdIS4TFp7ONk/uBDL2KhCVP18TpIYsUPKvvt1ySFVDpNaLymV79DEjkE8q3wA+07z6v+EPGHgw0wQF+wWnj2/Fcj9M0SnXnmHzt7y3Bdi4Tx/6D0O6A5BWVGl7U9NnFOACj7F6yZbBbG1SZpxmC5aXD4XGVwipf0W4Sss1I36zQKBWH7XEqy0f8sn0FfqZA85wfKdER4WAAZWV05omGJQXfa8cAYDJoAMOGGTmA91laaKphwum2VZ3N3lzgRKcNWqHoveaRRx8khkpRRwSkWBZaHRYKw1oXq/5PCFrBWTslCed2n8QMTi6+w2YnPq6TTVlYh4fLK5v9hJQl2W5EIJ+DZAHHwgS2K6p3Gia5gDHVQNpO7P/OMKUgMLyD4fyvtMf94yooClfUfadabiimBpDxcry2Ws4ngS4J5oOPeXPWY1cfqWXMCrt1G6plB5ykBGcA/D5IGUHLYymUQvl/4YwbbPFQncxuTpupJgrPFCEFMlsZHBdKvykwr9jwoQCPs8WSJY6DelMtIFKKLRnsQLMjglhhp3MF0ZEr8J2s8imigmUWff8eU4HIuoT4dv2wO0bJYYciMi1O+JA3xnW3g44+UjS27oU6vFVjPrQ7Uc9P1H4QUvbD4joJJrF72rTLYvvixp9S61NpsS31mUr9E4Dnu8+nuG5duzGNExmO+xhMjpF58O+BxGkgYxEJxAGgxUVj8Swmza78JQqEsdunFMPcD/bGQ9q1wv7MGsBvv1i3Og9q2xwN1pRc9uvdXx0FtSyl/qHDukNBVNoZZqFuM18tXoSgnKgh1BFAUnn64XPeahQZe3PwCtPn2cgsrIrCaZ0tdfv+iwl8Ora/CMUrnAkixOX2XWgfiChbrQW0w2W3Gas8wMAaUB/kv67yuP8BZAYmxwxAOn5OtPkS690INObetLHHhgfdF3dT+zxiVaRI8GbA+VngjEzvi5ghHyAsB5NBXAwbsbuTdGtNkrB7X1+4NDskJcOko7C9svX2cDZC3SFEwsUM4qLwPhXHB1jFOHUtWeq8cCnLIhXscXgQl2gnwRFzfEboxOoEprYWZWnrPh7qTA+VXvtH5/4gVXYi5cZi+3kiOU9If7QqWYmz9dbXKacy8I1PpkLrjOaK0VvT0mAzXwkhmJ6/zclRxTX8CRK+uDdQ==";

    @Test
    public void shouldReturnSuccessWhenVCOPayment() {
        Context context = InstrumentationRegistry.getContext();

        JudoApiService apiService = getJudo().getApiService(context);
        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.vcoPayment(getVCOPaymentRequest()).subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().get(0).isSuccess(), is(true));
    }

    @Test
    public void shouldReturnSuccessWhenVCOPreAuth() {
        Context context = InstrumentationRegistry.getContext();

        JudoApiService apiService = getJudo().getApiService(context);
        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.vcoPreAuth(getVCOPaymentRequest()).subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().get(0).isSuccess(), is(true));
    }

    private VCOPaymentRequest getVCOPaymentRequest() {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("meta", "data");

        return new VCOPaymentRequest.Builder()
                .setJudoId(JUDO_ID_IRIDIUM)
                .setAmount("0.10")
                .setCurrency(Currency.GBP)
                .setConsumerReference("VCOTest")
                .setMetaData(metaData)
                .setVCOWallet(new VCOWallet.Builder()
                        .setCallId(CALL_ID)
                        .setEncryptedKey(ENC_KEY)
                        .setEncryptedPaymentData(ENC_PAYMENT_DATA)
                        .build())
                .build();
    }
}
