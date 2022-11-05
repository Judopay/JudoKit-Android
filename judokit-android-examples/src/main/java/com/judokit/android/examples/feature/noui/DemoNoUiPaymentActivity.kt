package com.judokit.android.examples.feature.noui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judokit.android.examples.R
import com.judokit.android.examples.databinding.ActivityDemoNoUiPaymentBinding
import com.judokit.android.examples.databinding.ViewSecurityCodeInputBinding
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
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.toIntent
import com.judopay.judokit.android.service.CardTransactionManager
import com.judopay.judokit.android.service.CardTransactionManagerResultListener
import com.judopay.judokit.android.ui.common.ButtonState

private const val REGISTER_CARD_REQUEST_CODE = 2

sealed class ActivityState {
    object Idle : ActivityState()
    object PayWithToken : ActivityState()
    object PayWithPreAuthToken : ActivityState()
    object PayWithCard : ActivityState()
}

class DemoNoUiPaymentActivity : AppCompatActivity(), CardTransactionManagerResultListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var service: JudoApiService
    private lateinit var transactionDetailsBuilder: TransactionDetails.Builder
    private val caller = DemoNoUiPaymentActivity::class.java.name
    private lateinit var binding: ActivityDemoNoUiPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoNoUiPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val judo = intent.getParcelableExtra<Judo>(JUDO_OPTIONS)
            ?: throw IllegalStateException("Judo object is required")

        service = JudoApiServiceFactory.createApiService(this, judo)
        binding.tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
        binding.preAuthTokenPaymentButton.state = ButtonState.Disabled(R.string.preauth_token_payment)
        transactionDetailsBuilder = TransactionDetails.Builder()
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
            .setState(judo.address?.state)

        binding.cardPaymentButton.setOnClickListener {
            handleState(ActivityState.PayWithCard)
            CardTransactionManager.getInstance(this).payment(
                transactionDetailsBuilder
                    .setCardNumber("4000023104662535")
                    .setExpirationDate("12/25")
                    .setCardHolderName("CHALLENGE")
                    .build(),
                caller
            )
        }

        binding.createCardTokenButton.setOnClickListener {
            val intent = Intent(this@DemoNoUiPaymentActivity, JudoActivity::class.java)
            intent.putExtra(JUDO_OPTIONS, getJudo(judo, PaymentWidgetType.CREATE_CARD_TOKEN))
            startActivityForResult(intent, REGISTER_CARD_REQUEST_CODE)
        }

        binding.tokenPaymentButton.setOnClickListener {
            askForSecurityCode { code, name ->
                if (code.isNullOrEmpty() && name.isNullOrEmpty()) {
                    return@askForSecurityCode
                }

                transactionDetailsBuilder.setSecurityNumber(code)
                transactionDetailsBuilder.setCardHolderName(name)
                handleState(ActivityState.PayWithToken)
                CardTransactionManager.getInstance(this).paymentWithToken(transactionDetailsBuilder.build(), caller)
            }
        }

        binding.preAuthTokenPaymentButton.setOnClickListener {
            askForSecurityCode { code, name ->
                if (code.isNullOrEmpty() && name.isNullOrEmpty()) {
                    return@askForSecurityCode
                }

                transactionDetailsBuilder.setSecurityNumber(code)
                transactionDetailsBuilder.setCardHolderName(name)
                handleState(ActivityState.PayWithPreAuthToken)
                CardTransactionManager.getInstance(this).preAuthWithToken(transactionDetailsBuilder.build(), caller)
            }
        }

        CardTransactionManager.getInstance(this).registerResultListener(this)
    }

    private fun askForSecurityCode(completionCallback: (String?, String?) -> Unit) {
        val inflater = LayoutInflater.from(this)
        val securityCodeInputBinding = ViewSecurityCodeInputBinding.inflate(inflater)

        MaterialAlertDialogBuilder(this)
            .setTitle("Enter your security code")
            .setView(securityCodeInputBinding.root)
            .setNegativeButton("Cancel") { _, _ ->
                completionCallback(null, null)
            }
            .setPositiveButton("Proceed") { _, _ ->
                val code = securityCodeInputBinding.securityCodeEditText.text.toString()
                val name = securityCodeInputBinding.cardHolderEditText.text.toString()

                completionCallback(code, name)
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER_CARD_REQUEST_CODE) {
            binding.createCardTokenButton.state = ButtonState.Enabled(R.string.create_card_token)
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
        CardTransactionManager.getInstance(this).unRegisterResultListener(this)
        super.onDestroy()
    }

    override fun onCardTransactionResult(result: JudoPaymentResult) {
        runOnUiThread { handleState(ActivityState.Idle) }
        setResult(result.code, result.toIntent())
        finish()
    }

    private fun handleState(state: ActivityState) {
        when (state) {
            is ActivityState.Idle -> {
                binding.tokenPaymentButton.state = ButtonState.Enabled(R.string.token_payment)
                binding.preAuthTokenPaymentButton.state = ButtonState.Enabled(R.string.preauth_token_payment)
                binding.cardPaymentButton.state = ButtonState.Enabled(R.string.feature_title_payment)
            }
            is ActivityState.PayWithToken -> {
                binding.tokenPaymentButton.state = ButtonState.Loading
                binding.cardPaymentButton.state = ButtonState.Disabled(R.string.feature_title_payment)
                binding.preAuthTokenPaymentButton.state = ButtonState.Disabled(R.string.preauth_token_payment)
            }
            is ActivityState.PayWithPreAuthToken -> {
                binding.tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
                binding.cardPaymentButton.state = ButtonState.Disabled(R.string.feature_title_payment)
                binding.preAuthTokenPaymentButton.state = ButtonState.Loading
            }
            is ActivityState.PayWithCard -> {
                binding.cardPaymentButton.state = ButtonState.Loading
                binding.tokenPaymentButton.state = ButtonState.Disabled(R.string.token_payment)
                binding.preAuthTokenPaymentButton.state = ButtonState.Disabled(R.string.preauth_token_payment)
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
            .setThreeDSTwoMaxTimeout(threeDSTwoMaxTimeout)
            .setThreeDSTwoMessageVersion(threeDSTwoMessageVersion)
            .build()
    }
}
