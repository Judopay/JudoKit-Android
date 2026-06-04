package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.american
import com.judopay.judokit.android.model.canadian
import com.judopay.judokit.android.model.chinese
import com.judopay.judokit.android.model.indian
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CANADA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_CHINA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_INDIA
import com.judopay.judokit.android.ui.common.ALPHA_2_CODE_US
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Test Country.adminDivisionConfig()")
internal class AdminDivisionConfigTest {
    private fun makeCountry(alpha2Code: String) =
        Country(
            alpha2Code = alpha2Code,
            name = "Test",
            dialCode = "1",
            numericCode = "001",
            phoneNumberFormat = null,
        )

    @Test
    @DisplayName("Given CA country, then returns canadian divisions with CA province hint")
    fun returnsCanadianDivisionsForCA() {
        val config = makeCountry(ALPHA_2_CODE_CANADA).adminDivisionConfig()
        assertEquals(AdminDivisionConfig(canadian, R.string.jp_ca_province_hint), config)
    }

    @Test
    @DisplayName("Given CN country, then returns chinese divisions with CN province hint")
    fun returnsChineseDivisionsForCN() {
        val config = makeCountry(ALPHA_2_CODE_CHINA).adminDivisionConfig()
        assertEquals(AdminDivisionConfig(chinese, R.string.jp_cn_province_hint), config)
    }

    @Test
    @DisplayName("Given IN country, then returns indian divisions with IN state hint")
    fun returnsIndianDivisionsForIN() {
        val config = makeCountry(ALPHA_2_CODE_INDIA).adminDivisionConfig()
        assertEquals(AdminDivisionConfig(indian, R.string.jp_in_state_hint), config)
    }

    @Test
    @DisplayName("Given US country, then returns american divisions with US state hint")
    fun returnsAmericanDivisionsForUS() {
        val config = makeCountry(ALPHA_2_CODE_US).adminDivisionConfig()
        assertEquals(AdminDivisionConfig(american, R.string.jp_us_state_hint), config)
    }

    @Test
    @DisplayName("Given unknown country code, then returns null")
    fun returnsNullForUnknownCountry() {
        val config = makeCountry("GB").adminDivisionConfig()
        assertNull(config)
    }
}
