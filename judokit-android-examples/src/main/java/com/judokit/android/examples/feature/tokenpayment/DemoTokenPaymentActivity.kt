package com.judokit.android.examples.feature.tokenpayment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.judokit.android.JUDO_OPTIONS
import com.judokit.android.JUDO_RESULT
import com.judokit.android.Judo
import com.judokit.android.JudoActivity
import com.judokit.android.PAYMENT_CANCELLED
import com.judokit.android.PAYMENT_ERROR
import com.judokit.android.PAYMENT_SUCCESS
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.api.createTokenRequest
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.api.model.response.toJudoPaymentResult
import com.judokit.android.examples.R
import com.judokit.android.examples.feature.JUDO_PAYMENT_WIDGET_REQUEST_CODE
import com.judokit.android.model.Amount
import com.judokit.android.model.Currency
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.JudoResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.code
import com.judokit.android.model.toIntent
import com.judokit.android.ui.common.ButtonState
import kotlinx.android.synthetic.main.activity_demo_token_payment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val REGISTER_CARD_REQUEST_CODE = 2

sealed class ActivityState {
    object Idle : ActivityState()
    object PayWithToken : ActivityState()
    object PayWithPreAuthToken : ActivityState()
}

class DemoTokenPaymentActivity : AppCompatActivity(), Callback<JudoApiCallResult<Receipt>> {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cardToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_token_payment)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val judo = intent.getParcelableExtra<Judo>(JUDO_OPTIONS)
            ?: throw IllegalStateException("Judo object is required")

        val service = JudoApiServiceFactory.createApiService(this, judo)

        tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
        preAuthTokenPaymentButton.state = ButtonState.Disabled(R.string.preauth_token_payment)

        createCardTokenButton.setOnClickListener {
            val intent = Intent(this@DemoTokenPaymentActivity, JudoActivity::class.java)
            intent.putExtra(JUDO_OPTIONS, judo)
            startActivityForResult(intent, REGISTER_CARD_REQUEST_CODE)
        }

        tokenPaymentButton.setOnClickListener {
            handleState(ActivityState.PayWithToken)
            service.tokenPayment(
                createTokenRequest(
                    getJudo(judo),
                    cardToken,
                    "452"
                )
            )
                .enqueue(this@DemoTokenPaymentActivity)
        }

        preAuthTokenPaymentButton.setOnClickListener {
            handleState(ActivityState.PayWithPreAuthToken)
            service.preAuthTokenPayment(
                createTokenRequest(
                    getJudo(judo),
                    cardToken,
                    "452"
                )
            )
                .enqueue(this@DemoTokenPaymentActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER_CARD_REQUEST_CODE) {
            createCardTokenButton.state = ButtonState.Enabled(R.string.create_card_token)
            when (resultCode) {
                PAYMENT_SUCCESS -> {
                    val result = data?.getParcelableExtra<JudoResult>(JUDO_RESULT)
                    val token = result?.cardDetails?.token
                    if (token.isNullOrBlank()) {
                        Toast.makeText(this, "Card token not found", Toast.LENGTH_SHORT).show()
                    } else {
                        cardToken = token
                    }
                    handleState(ActivityState.Idle)
                }

                PAYMENT_CANCELLED,
                PAYMENT_ERROR -> {
                    setResult(resultCode, data)
                    finish()
                }
            }
        }
        if (requestCode == JUDO_PAYMENT_WIDGET_REQUEST_CODE) {
            setResult(resultCode, data)
            finish()
        }
    }

    override fun onResponse(
        call: Call<JudoApiCallResult<Receipt>>,
        response: Response<JudoApiCallResult<Receipt>>
    ) {
        when (val result = response.body()?.toJudoPaymentResult()) {
            is JudoPaymentResult.Error,
            is JudoPaymentResult.Success -> {
                setResult(result.code, result.toIntent())
                finish()
            }
        }
    }

    override fun onFailure(call: Call<JudoApiCallResult<Receipt>>, t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
        handleState(ActivityState.Idle)
    }

    private fun handleState(state: ActivityState) {
        when (state) {
            is ActivityState.Idle -> {
                tokenPaymentButton.state = ButtonState.Enabled(R.string.token_payment)
                preAuthTokenPaymentButton.state =
                    ButtonState.Enabled(R.string.preauth_token_payment)
            }
            is ActivityState.PayWithToken -> {
                tokenPaymentButton.state = ButtonState.Loading
                preAuthTokenPaymentButton.state =
                    ButtonState.Disabled(R.string.preauth_token_payment)
            }
            is ActivityState.PayWithPreAuthToken -> {
                tokenPaymentButton.state = ButtonState.Disabled(R.string.preauth_token_payment)
                preAuthTokenPaymentButton.state = ButtonState.Loading
            }
        }
    }

    private val amount: Amount
        get() {
            val amountValue = sharedPreferences.getString("amount", null)
            val currency = sharedPreferences.getString("currency", null)
            val myCurrency = if (!currency.isNullOrEmpty()) {
                Currency.valueOf(currency)
            } else Currency.GBP

            return Amount.Builder()
                .setAmount(amountValue)
                .setCurrency(myCurrency)
                .build()
        }

    private fun getJudo(judo: Judo): Judo = with(judo) {
        Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
            .setJudoId(judoId)
            .setAuthorization(authorization)
            .setIsSandboxed(isSandboxed)
            .setAmount(this@DemoTokenPaymentActivity.amount)
            .setReference(reference)
            .setUiConfiguration(uiConfiguration)
            .setPaymentMethods(paymentMethods)
            .setSupportedCardNetworks(supportedCardNetworks)
            .setPrimaryAccountDetails(primaryAccountDetails)
            .setGooglePayConfiguration(googlePayConfiguration)
            .setAddress(address)
            .setPBBAConfiguration(pbbaConfiguration)
            .build()
    }
}
