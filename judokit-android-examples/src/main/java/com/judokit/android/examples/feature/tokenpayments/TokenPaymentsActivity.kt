package com.judokit.android.examples.feature.tokenpayments

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.judokit.android.examples.common.parcelable
import com.judokit.android.examples.common.startResultActivity
import com.judokit.android.examples.common.toResult
import com.judokit.android.examples.theme.JudoKitAndroidTheme
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivityResultContracts
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.USER_CANCELLED
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.toIntent

class TokenPaymentsActivity : ComponentActivity() {
    private val viewModel = TokenPaymentsViewModel()
    private val tokenizeCardLauncher =
        registerForActivityResult(JudoActivityResultContracts.CreateCardToken()) { result ->
            handleTokenizeNewCardResult(result)
        }
    private val tokenPaymentLauncher =
        registerForActivityResult(JudoActivityResultContracts.CardTokenPayment()) { result ->
            setResult(result.code, result.toIntent())
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)

        val judo = intent.parcelable<Judo>(JUDO_OPTIONS)
        check(judo != null) { "Judo object is required" }

        viewModel.initialJudoConfig = judo

        setContent {
            JudoKitAndroidTheme {
                TokenPaymentsScreen(
                    viewModel = viewModel,
                    onTokenizeNewCard = { tokenizeNewCard() },
                    onTokenPayment = { makeTokenPayment(isPreAuth = false) },
                    onTokenPreAuth = { makeTokenPayment(isPreAuth = true) },
                    onClose = { finish() },
                )
            }
        }
    }

    private fun tokenizeNewCard() {
        viewModel.isBusy = true
        tokenizeCardLauncher.launch(viewModel.getJudo(PaymentWidgetType.CREATE_CARD_TOKEN))
    }

    private fun handleTokenizeNewCardResult(result: JudoPaymentResult) {
        viewModel.isBusy = false
        when (result) {
            is JudoPaymentResult.Success -> {
                val token = result.result.cardDetails?.token
                if (token.isNullOrBlank()) {
                    Toast.makeText(this, "Card token not found", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.scheme = result.result.cardDetails
                        ?.type
                        ?.toString() ?: "0"
                    viewModel.token = token
                    viewModel.lastFour = result.result.cardDetails?.lastFour ?: ""
                    viewModel.cardholderName = result.result.cardDetails?.cardHolderName ?: ""
                }
            }
            is JudoPaymentResult.Error -> handleError(result.error)
            is JudoPaymentResult.UserCancelled -> handleError(result.error)
        }
    }

    private fun handleError(error: JudoError) {
        if (error.code == USER_CANCELLED && error.details.isEmpty()) {
            Toast.makeText(this, "User cancelled the payment.", Toast.LENGTH_SHORT).show()
        } else {
            startResultActivity(error.toResult())
        }
    }

    private fun makeTokenPayment(isPreAuth: Boolean) {
        viewModel.isBusy = true
        val widgetType = if (isPreAuth) PaymentWidgetType.TOKEN_PRE_AUTH else PaymentWidgetType.TOKEN_PAYMENT
        tokenPaymentLauncher.launch(viewModel.getJudo(widgetType))
    }
}
