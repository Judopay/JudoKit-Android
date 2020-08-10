package com.judokit.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.model.CardScanResultType
import com.judokit.android.model.CardScanningResult
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.code
import com.judokit.android.model.googlepay.GooglePayEnvironment
import com.judokit.android.model.isExposed
import com.judokit.android.model.isGooglePayWidget
import com.judokit.android.model.isPaymentMethodsWidget
import com.judokit.android.model.navigationGraphId
import com.judokit.android.model.toCardScanningResult
import com.judokit.android.model.toIntent
import com.judokit.android.service.JudoGooglePayService
import com.judokit.android.ui.common.showAlert

internal const val LOAD_GPAY_PAYMENT_DATA_REQUEST_CODE = Activity.RESULT_FIRST_USER + 1
internal const val SCAN_CARD_REQUEST_CODE = Activity.RESULT_FIRST_USER + 2

class JudoActivity : AppCompatActivity() {

    private lateinit var viewModel: JudoSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.judopay_activity)

        // Treat the content of the window as secure, preventing it from appearing in screenshots
        // or from being viewed on non-secure displays.
        val secureFlag = WindowManager.LayoutParams.FLAG_SECURE
        window.setFlags(secureFlag, secureFlag)

        // setup shared view-model & response callbacks
        val judoApiService = JudoApiServiceFactory.createApiService(applicationContext, judo)
        val factory = JudoSharedViewModelFactory(judo, buildJudoGooglePayService(), judoApiService)

        viewModel = ViewModelProvider(this, factory).get(JudoSharedViewModel::class.java)
        viewModel.paymentResult.observe(this, Observer { dispatchPaymentResult(it) })

        viewModel.bankPaymentResult.observe(
            this,
            Observer {
                if (judo.paymentWidgetType.isPaymentMethodsWidget) {
                    viewModel.paymentMethodsResult.postValue(it)
                } else {
                    viewModel.paymentResult.postValue(it)
                }
            }
        )

        if (judo.paymentWidgetType.isGooglePayWidget) {
            viewModel.send(JudoSharedAction.LoadGPayPaymentData)
            return
        }

        // setup navigation graph
        val graphId = judo.paymentWidgetType.navigationGraphId
        val navigationHost = NavHostFragment.create(graphId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, navigationHost)
            .setPrimaryNavigationFragment(navigationHost)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOAD_GPAY_PAYMENT_DATA_REQUEST_CODE -> dispatchGPayResult(resultCode, data)
            SCAN_CARD_REQUEST_CODE -> dispatchScanCardResult(resultCode, data)
            else -> Log.i("JudoActivity", "Received unsupported requestCode: $requestCode")
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun dispatchGPayResult(resultCode: Int, data: Intent?) {
        val action = when (resultCode) {
            Activity.RESULT_OK -> {
                val paymentData = if (data != null) PaymentData.getFromIntent(data) else null

                if (paymentData != null) {
                    JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData)
                } else {
                    JudoSharedAction.LoadGPayPaymentDataError("Null response data.")
                }
            }

            AutoResolveHelper.RESULT_ERROR -> {
                val status = AutoResolveHelper.getStatusFromIntent(data)
                val message = status?.statusMessage ?: "Unknown error"
                JudoSharedAction.LoadGPayPaymentDataError(message)
            }

            Activity.RESULT_CANCELED -> JudoSharedAction.LoadGPayPaymentDataUserCancelled

            else -> {
                JudoSharedAction.LoadGPayPaymentDataError("Unknown error")
            }
        }

        viewModel.send(action)
    }

    private fun dispatchScanCardResult(resultCode: Int, data: Intent?) {
        val card = data?.getParcelableExtra<Card>(ScanCardIntent.RESULT_PAYCARDS_CARD)

        val resultType = when (resultCode) {
            Activity.RESULT_OK -> CardScanResultType.SUCCESS
            Activity.RESULT_CANCELED -> CardScanResultType.CANCELLED
            else -> CardScanResultType.ERROR
        }

        val result = card?.toCardScanningResult() ?: CardScanningResult(resultType)
        viewModel.send(JudoSharedAction.ScanCardResult(result))
    }

    private fun dispatchPaymentResult(result: JudoPaymentResult) {
        when (result) {
            is JudoPaymentResult.Success -> {
                setResult(result.code, result.toIntent())
                finish()
            }
            is JudoPaymentResult.Error -> {
                with(viewModel.error) {
                    code = result.error.code
                    message = result.error.message
                    details.add(result.error)
                }

                if (judo.paymentWidgetType.isExposed) {
                    setResult(result.code, result.toIntent())
                    finish()
                    return
                }

                showAlert(this, result.error.message)
            }
            is JudoPaymentResult.UserCancelled -> {
                with(viewModel.error) {
                    code = result.error.code
                    message = result.error.message
                }
                setResult(result.code, result.apply { error = viewModel.error }.toIntent())
                finish()
            }
        }
    }

    private fun buildJudoGooglePayService(): JudoGooglePayService {
        val environment = when (judo.googlePayConfiguration?.environment) {
            GooglePayEnvironment.PRODUCTION -> WalletConstants.ENVIRONMENT_PRODUCTION
            else -> WalletConstants.ENVIRONMENT_TEST
        }

        val walletOptions = Wallet.WalletOptions.Builder().setEnvironment(environment).build()
        val client = Wallet.getPaymentsClient(this, walletOptions)
        return JudoGooglePayService(client, this, judo)
    }
}
