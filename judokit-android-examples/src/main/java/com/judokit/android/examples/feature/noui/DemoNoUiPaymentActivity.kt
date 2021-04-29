package com.judokit.android.examples.feature.noui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.judokit.android.examples.R
import com.judokit.android.examples.feature.JUDO_PAYMENT_WIDGET_REQUEST_CODE
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.JUDO_RESULT
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivity
import com.judopay.judokit.android.PAYMENT_CANCELLED
import com.judopay.judokit.android.PAYMENT_ERROR
import com.judopay.judokit.android.PAYMENT_SUCCESS
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetail
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.toIntent
import com.judopay.judokit.android.service.CardTransactionCallback
import com.judopay.judokit.android.service.CardTransactionService
import com.judopay.judokit.android.ui.common.ButtonState
import kotlinx.android.synthetic.main.activity_demo_no_ui_payment.*

private const val REGISTER_CARD_REQUEST_CODE = 2

sealed class ActivityState {
    object Idle : ActivityState()
    object PayWithToken : ActivityState()
    object PayWithPreAuthToken : ActivityState()
    object PayWithCard : ActivityState()
}

class DemoNoUiPaymentActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var service: JudoApiService
    private lateinit var cardTransactionService: CardTransactionService
    private lateinit var transactionDetailsBuilder: TransactionDetail.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_no_ui_payment)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val judo = intent.getParcelableExtra<Judo>(JUDO_OPTIONS)
            ?: throw IllegalStateException("Judo object is required")

        service = JudoApiServiceFactory.createApiService(this, judo)
        tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
        preAuthTokenPaymentButton.state = ButtonState.Disabled(R.string.preauth_token_payment)
        transactionDetailsBuilder = TransactionDetail.Builder()
            .setSecurityNumber("452")
            .setEmail(judo.emailAddress)
            .setCountryCode(judo.address?.countryCode.toString())
            .setPhoneCountryCode(judo.phoneCountryCode)
            .setMobileNumber(judo.mobileNumber)
            .setAddressLine1(judo.address?.line1)
            .setAddressLine2(judo.address?.line2)
            .setAddressLine3(judo.address?.line3)
            .setCity(judo.address?.town)
            .setPostalCode(judo.address?.postCode)

        cardPaymentButton.setOnClickListener {
            cardTransactionService = CardTransactionService(
                this,
                getJudo(judo, PaymentWidgetType.CARD_PAYMENT),
                service
            )
            handleState(ActivityState.PayWithCard)
            cardTransactionService.makeTransaction(
                transactionDetailsBuilder
                    .setCardNumber("4111111111111111")
                    .setExpirationDate("12/25")
                    .setCardHolderName("name")
                    .build(),
                object : CardTransactionCallback {
                    override fun onFinish(result: JudoPaymentResult) {
                        handleState(ActivityState.Idle)
                        setResult(result.code, result.toIntent())
                        finish()
                    }
                }
            )
        }

        createCardTokenButton.setOnClickListener {
            val intent = Intent(this@DemoNoUiPaymentActivity, JudoActivity::class.java)
            intent.putExtra(JUDO_OPTIONS, getJudo(judo, PaymentWidgetType.CREATE_CARD_TOKEN))
            startActivityForResult(intent, REGISTER_CARD_REQUEST_CODE)
        }
        tokenPaymentButton.setOnClickListener {
            cardTransactionService = CardTransactionService(
                this,
                getJudo(judo, PaymentWidgetType.CARD_PAYMENT),
                service
            )
            handleState(ActivityState.PayWithToken)
            cardTransactionService.tokenPayment(
                transactionDetailsBuilder.build(),
                object : CardTransactionCallback {
                    override fun onFinish(result: JudoPaymentResult) {
                        handleState(ActivityState.Idle)
                        setResult(result.code, result.toIntent())
                        finish()
                    }
                }
            )
        }

        preAuthTokenPaymentButton.setOnClickListener {
            cardTransactionService = CardTransactionService(
                this,
                getJudo(judo, PaymentWidgetType.PRE_AUTH),
                service
            )
            handleState(ActivityState.PayWithPreAuthToken)
            cardTransactionService.tokenPayment(
                transactionDetailsBuilder.build(),
                object : CardTransactionCallback {
                    override fun onFinish(result: JudoPaymentResult) {
                        handleState(ActivityState.Idle)
                        setResult(result.code, result.toIntent())
                        finish()
                    }
                }
            )
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
                    val type = result?.cardDetails?.type
                    val lastFour = result?.cardDetails?.lastFour
                    if (token.isNullOrBlank()) {
                        Toast.makeText(this, "Card token not found", Toast.LENGTH_SHORT).show()
                    } else {
                        transactionDetailsBuilder
                            .setCardToken(token)
                            .setCardType(CardNetwork.withIdentifier(type ?: 0))
                            .setCardLastFour(lastFour)
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

    override fun onDestroy() {
        cardTransactionService.close()
        super.onDestroy()
    }

    private fun handleState(state: ActivityState) {
        when (state) {
            is ActivityState.Idle -> {
                tokenPaymentButton.state = ButtonState.Enabled(R.string.token_payment)
                preAuthTokenPaymentButton.state =
                    ButtonState.Enabled(R.string.preauth_token_payment)
                cardPaymentButton.state =
                    ButtonState.Enabled(R.string.feature_title_payment)
            }
            is ActivityState.PayWithToken -> {
                tokenPaymentButton.state = ButtonState.Loading
                cardPaymentButton.state =
                    ButtonState.Disabled(R.string.feature_title_payment)
                preAuthTokenPaymentButton.state =
                    ButtonState.Disabled(R.string.preauth_token_payment)
            }
            is ActivityState.PayWithPreAuthToken -> {
                tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
                cardPaymentButton.state = ButtonState.Disabled(R.string.feature_title_payment)
                preAuthTokenPaymentButton.state = ButtonState.Loading
            }
            is ActivityState.PayWithCard -> {
                cardPaymentButton.state = ButtonState.Loading
                tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
                preAuthTokenPaymentButton.state =
                    ButtonState.Disabled(R.string.preauth_token_payment)
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

    private fun getJudo(judo: Judo, widgetType: PaymentWidgetType): Judo = with(judo) {
        Judo.Builder(widgetType)
            .setJudoId(judoId)
            .setAuthorization(authorization)
            .setIsSandboxed(isSandboxed)
            .setAmount(this@DemoNoUiPaymentActivity.amount)
            .setReference(reference)
            .setUiConfiguration(uiConfiguration)
            .setPaymentMethods(paymentMethods)
            .setSupportedCardNetworks(supportedCardNetworks)
            .setPrimaryAccountDetails(primaryAccountDetails)
            .setGooglePayConfiguration(googlePayConfiguration)
            .setAddress(address)
            .setPBBAConfiguration(pbbaConfiguration)
            .setScaExemption(scaExemption)
            .setChallengeRequestIndicator(challengeRequestIndicator)
            .build()
    }
}
