package com.judokit.android.examples.feature.tokenpayments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.judokit.android.examples.common.parcelable
import com.judokit.android.examples.common.startResultActivity
import com.judokit.android.examples.common.toResult
import com.judokit.android.examples.theme.JudoKitAndroidTheme
import com.judopay.judokit.android.JUDO_ERROR
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.JUDO_RESULT
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivity
import com.judopay.judokit.android.PAYMENT_CANCELLED
import com.judopay.judokit.android.PAYMENT_ERROR
import com.judopay.judokit.android.PAYMENT_SUCCESS
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.USER_CANCELLED

class TokenPaymentsActivity : ComponentActivity() {
    private val viewModel = TokenPaymentsViewModel()
    private val tokenizeCardLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleTokenizeNewCardResult(it)
        }
    private val tokenPaymentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            setResult(it.resultCode, it.data)
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val judo = intent.parcelable<Judo>(JUDO_OPTIONS)
        check(judo != null) { "Judo object is required" }

        viewModel.initialJudoConfig = judo

        setContent {
            JudoKitAndroidTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
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
    }

    private fun tokenizeNewCard() {
        viewModel.isBusy = true
        val intent = Intent(this, JudoActivity::class.java)
        val judoOptions = viewModel.getJudo(PaymentWidgetType.CREATE_CARD_TOKEN)
        intent.putExtra(JUDO_OPTIONS, judoOptions)
        tokenizeCardLauncher.launch(intent)
    }

    private fun handleTokenizeNewCardResult(result: ActivityResult) {
        viewModel.isBusy = false
        when (result.resultCode) {
            PAYMENT_SUCCESS -> {
                val judoResult = result.data?.parcelable<JudoResult>(JUDO_RESULT)
                val token = judoResult?.cardDetails?.token
                if (token.isNullOrBlank()) {
                    Toast.makeText(this, "Card token not found", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.scheme = judoResult.cardDetails?.type?.toString() ?: "0"
                    viewModel.token = token
                    viewModel.lastFour = judoResult.cardDetails?.lastFour ?: ""
                    viewModel.cardholderName = judoResult.cardDetails?.cardHolderName ?: ""
                }
            }
            PAYMENT_CANCELLED,
            PAYMENT_ERROR,
            -> {
                handleError(result)
            }
        }
    }

    private fun handleError(result: ActivityResult) {
        val error = result.data?.parcelable<JudoError>(JUDO_ERROR)
        if (error != null) {
            if (error.code == USER_CANCELLED && error.details.isEmpty()) {
                Toast.makeText(this, "User cancelled the payment.", Toast.LENGTH_SHORT).show()
            } else {
                startResultActivity(error.toResult())
            }
        }
    }

    private fun makeTokenPayment(isPreAuth: Boolean) {
        viewModel.isBusy = true
        val intent = Intent(this, JudoActivity::class.java)
        val widgetType = if (isPreAuth) PaymentWidgetType.TOKEN_PRE_AUTH else PaymentWidgetType.TOKEN_PAYMENT
        val judoOptions = viewModel.getJudo(widgetType)
        intent.putExtra(JUDO_OPTIONS, judoOptions)
        tokenPaymentLauncher.launch(intent)
    }
}
