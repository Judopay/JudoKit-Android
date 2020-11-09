package com.judokit.android.ui.cardverification.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.judokit.android.R
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.model.CardVerificationModel
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.ui.cardverification.WebViewCallback
import com.judokit.android.ui.cardverification.model.WebViewAction
import kotlinx.android.synthetic.main.three_ds_one_card_verification_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ThreeDSOneCompletionCallback {
    fun onSuccess(success: JudoPaymentResult)
    fun onFailure(error: JudoPaymentResult)
}

class ThreeDSOneCardVerificationView constructor(
    context: Context,
    val service: JudoApiService
) : Dialog(context, R.style.JudoTheme_FullscreenDialog), WebViewCallback {

    private lateinit var completionCallback: ThreeDSOneCompletionCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.three_ds_one_card_verification_view)

        backButton.setOnClickListener {
            completionCallback.onFailure(JudoPaymentResult.UserCancelled())
            dismiss()
        }
    }

    fun show(
        model: CardVerificationModel,
        completionCallback: ThreeDSOneCompletionCallback
    ) {
        super.show()
        this.completionCallback = completionCallback
        cardVerificationWebView.view = this
        cardVerificationWebView.authorize(model)
    }

    override fun send(action: WebViewAction) {
        when (action) {
            is WebViewAction.OnPageStarted -> {
                threeDSTextView.visibility = View.VISIBLE
                threeDSProgressBar.visibility = View.VISIBLE
            }
            is WebViewAction.OnPageLoaded -> {
                cardVerificationWebView.visibility = View.VISIBLE
                threeDSTextView.visibility = View.GONE
                threeDSProgressBar.visibility = View.GONE
            }
            is WebViewAction.OnAuthorizationComplete -> {
                service.complete3dSecure(action.receiptId, action.cardVerificationResult)
                    .enqueue(object :
                        Callback<JudoApiCallResult<Receipt>> {

                        override fun onResponse(
                            call: Call<JudoApiCallResult<Receipt>>,
                            response: Response<JudoApiCallResult<Receipt>>
                        ) {
                            when (val result = response.body()) {
                                is JudoApiCallResult.Success -> {
                                    if (result.data != null) {
                                        val success =
                                            JudoPaymentResult.Success(result.data.toJudoResult())
                                        completionCallback.onSuccess(success)
                                    }
                                }

                                is JudoApiCallResult.Failure -> {
                                    if (result.error != null) {
                                        val error =
                                            JudoPaymentResult.Error(result.error.toJudoError())
                                        completionCallback.onFailure(error)
                                    }
                                }
                            }
                            dismiss()
                        }

                        override fun onFailure(
                            call: Call<JudoApiCallResult<Receipt>>,
                            t: Throwable
                        ) {
                            val error = JudoPaymentResult.Error(
                                JudoError(
                                    message = t.message
                                        ?: context.resources.getString(R.string.error_request_failed_reason)
                                )
                            )
                            completionCallback.onFailure(error)
                            dismiss()
                        }
                    })
            }
        }
    }

    override fun onBackPressed() {
        completionCallback.onFailure(JudoPaymentResult.UserCancelled())
        super.onBackPressed()
    }
}
