package com.judopay.judokit.android.ui.cardentry.model

import androidx.annotation.StringRes
import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.AdministrativeDivision
import com.judopay.judokit.android.model.american
import com.judopay.judokit.android.model.canadian
import com.judopay.judokit.android.model.chinese
import com.judopay.judokit.android.model.indian
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US

data class AdminDivisionConfig(
    val divisions: List<AdministrativeDivision>,
    @StringRes val hint: Int,
)

fun Country.adminDivisionConfig(): AdminDivisionConfig? =
    when (alpha2Code) {
        ALPHA_2_CODE_CANADA -> AdminDivisionConfig(canadian, R.string.jp_ca_province_hint)
        ALPHA_2_CODE_CHINA -> AdminDivisionConfig(chinese, R.string.jp_cn_province_hint)
        ALPHA_2_CODE_INDIA -> AdminDivisionConfig(indian, R.string.jp_in_state_hint)
        ALPHA_2_CODE_US -> AdminDivisionConfig(american, R.string.jp_us_state_hint)
        else -> null
    }
