package com.judopay.judokit.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.model.CardScanResultType
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.googlepay.GooglePayEnvironment
import com.judopay.judokit.android.model.isExposed
import com.judopay.judokit.android.model.isGooglePayWidget
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.model.navigationGraphId
import com.judopay.judokit.android.model.toCardScanningResult
import com.judopay.judokit.android.model.toIntent
import com.judopay.judokit.android.service.JudoGooglePayService
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.showAlert
import com.judopay.judokit.android.ui.paymentmethods.CARD_ENTRY_OPTIONS

internal const val LOAD_GPAY_PAYMENT_DATA_REQUEST_CODE = Activity.RESULT_FIRST_USER + 1
internal const val SCAN_CARD_REQUEST_CODE = Activity.RESULT_FIRST_USER + 2

/**
 * Entry point for invoking Judo SDK to make any of the defined
 * [payment journeys.][com.judopay.judokit.android.model.PaymentWidgetType]
 * Activity must be started with [startActivityForResult].
 *
 * ```
 * val intent = Intent(this, JudoActivity::class.java)
 * intent.putExtra(JUDO_OPTIONS, judo)
 * startActivityForResult(intent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
 * ```
 */
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
        val factory = JudoSharedViewModelFactory(
            judo,
            buildJudoGooglePayService(),
            judoApiService,
            application
        )

        viewModel = ViewModelProvider(this, factory).get(JudoSharedViewModel::class.java)
        viewModel.paymentResult.observe(this, { dispatchPaymentResult(it) })

        viewModel.bankPaymentResult.observe(
            this,
            {
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
        val bundle = if (graphId == R.navigation.judo_card_input_graph) {
            // Card entry fragment parameters
            bundleOf(
                CARD_ENTRY_OPTIONS to CardEntryOptions(shouldDisplayBillingDetails = judo.is3DS2Enabled && judo.paymentWidgetType != PaymentWidgetType.CREATE_CARD_TOKEN)
            )
        } else null
        val navigationHost = NavHostFragment.create(graphId, bundle)

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
