package com.judokit.android.examples.model

import androidx.annotation.StringRes
import com.judokit.android.examples.R

enum class DemoFeature(
    @StringRes val title: Int,
    @StringRes val surTitle: Int,
) {
    PAYMENT(R.string.feature_title_payment, R.string.feature_subtitle_payment),
    PREAUTH(R.string.feature_title_preauth, R.string.feature_subtitle_preauth),
    CHECK_CARD(R.string.feature_title_check_card, R.string.feature_subtitle_check_card),
    CREATE_CARD_TOKEN(R.string.feature_title_save_card, R.string.feature_subtitle_save_card),
    GOOGLE_PAY_PAYMENT(R.string.feature_title_google_pay_payment, R.string.feature_subtitle_google_pay_payment),
    GOOGLE_PAY_PREAUTH(R.string.feature_title_google_pay_preauth, R.string.feature_subtitle_google_pay_preauth),
    PAYMENT_METHODS(R.string.feature_title_payment_methods, R.string.feature_subtitle_payment_methods),
    PREAUTH_PAYMENT_METHODS(R.string.feature_title_preauth_payment_methods, R.string.feature_subtitle_preauth_payment_methods),
    SERVER_TO_SERVER_PAYMENT_METHODS(
        R.string.feature_title_server_to_server_payment_methods,
        R.string.feature_subtitle_server_to_server_payment_methods,
    ),
    TOKEN_PAYMENTS(R.string.feature_title_token_payments, R.string.feature_subtitle_token_payments),
    NO_UI(R.string.feature_title_payment_no_ui, R.string.feature_subtitle_payment_no_ui),
    GET_TRANSACTION_DETAILS(R.string.feature_title_get_transaction_details, R.string.feature_subtitle_get_transaction_details),
}
