package com.judopay.judokit.android.examples.feature.tokenpayment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.JUDO_RESULT
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivity
import com.judopay.judokit.android.PAYMENT_CANCELLED
import com.judopay.judokit.android.PAYMENT_ERROR
import com.judopay.judokit.android.PAYMENT_SUCCESS
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.toCardVerificationModel
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.examples.R
import com.judopay.judokit.android.examples.feature.JUDO_PAYMENT_WIDGET_REQUEST_CODE
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.toIntent
import com.judopay.judokit.android.toTokenRequest
import com.judopay.judokit.android.ui.cardverification.THREE_DS_ONE_DIALOG_FRAGMENT_TAG
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCardVerificationDialogFragment
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
import com.judopay.judokit.android.ui.common.ButtonState
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
    private lateinit var service: JudoApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_token_payment)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val judo = intent.getParcelableExtra<Judo>(JUDO_OPTIONS)
            ?: throw IllegalStateException("Judo object is required")

        service = JudoApiServiceFactory.createApiService(this, judo)

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
                getJudo(judo).toTokenRequest(cardToken, "452")
            )
                .enqueue(this@DemoTokenPaymentActivity)
        }

        preAuthTokenPaymentButton.setOnClickListener {
            handleState(ActivityState.PayWithPreAuthToken)
            service.preAuthTokenPayment(
                getJudo(judo).toTokenRequest(cardToken, "452")
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
        when (val apiResult = response.body()) {
            is JudoApiCallResult.Success -> {
                val receipt = apiResult.data
                if (receipt != null && receipt.is3dSecureRequired) {
                    val callback = object : ThreeDSOneCompletionCallback {
                        override fun onSuccess(success: JudoPaymentResult) {
                            setResult(success.code, success.toIntent())
                            finish()
                        }

                        override fun onFailure(error: JudoPaymentResult) {
                            setResult(error.code, error.toIntent())
                            finish()
                        }
                    }
                    val fragment = ThreeDSOneCardVerificationDialogFragment(
                        service,
                        receipt.toCardVerificationModel(),
                        callback
                    )
                    fragment.show(supportFragmentManager, THREE_DS_ONE_DIALOG_FRAGMENT_TAG)
                } else {
                    val result = apiResult.toJudoPaymentResult(resources)
                    setResult(result.code, result.toIntent())
                    finish()
                }
            }
            is JudoApiCallResult.Failure -> {
                val result = apiResult.toJudoPaymentResult(resources)
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
                tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
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
