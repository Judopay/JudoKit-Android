package com.judopay.judokit.android.service

import android.content.Context
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.ui.common.isDependencyPresent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@DisplayName("Testing RecommendationService.isRecommendationFeatureAvailable")
internal class RecommendationServiceTest {
    private val context: Context = mockk(relaxed = true)
    private val judo: Judo = mockk(relaxed = true)

    private lateinit var sut: RecommendationService

    @BeforeEach
    fun setUp() {
        mockkStatic("com.judopay.judokit.android.ui.common.FunctionsKt")
        sut = RecommendationService(context, judo)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("Returns false when recommendationConfiguration is null")
    fun returnFalseWhenRecommendationConfigurationIsNull() {
        every { isDependencyPresent(RAVELIN_ENCRYPT_CLASS_NAME) } returns true
        every { judo.recommendationConfiguration } returns null

        assertFalse(sut.isRecommendationFeatureAvailable(TransactionType.PAYMENT))
    }

    @Test
    @DisplayName("Returns false when Ravelin SDK is not available in classpath")
    fun returnFalseWhenRavelinNotAvailable() {
        every { isDependencyPresent(RAVELIN_ENCRYPT_CLASS_NAME) } returns false
        every { judo.recommendationConfiguration } returns mockk(relaxed = true)

        assertFalse(sut.isRecommendationFeatureAvailable(TransactionType.PAYMENT))
    }

    @ParameterizedTest
    @EnumSource(TransactionType::class, names = ["SAVE", "PAYMENT_WITH_TOKEN", "PRE_AUTH_WITH_TOKEN"])
    @DisplayName("Returns false for unsupported transaction types")
    fun returnFalseForUnsupportedTransactionTypes(type: TransactionType) {
        every { isDependencyPresent(RAVELIN_ENCRYPT_CLASS_NAME) } returns true
        every { judo.recommendationConfiguration } returns mockk(relaxed = true)

        assertFalse(sut.isRecommendationFeatureAvailable(type))
    }

    @ParameterizedTest
    @EnumSource(TransactionType::class, names = ["PAYMENT", "CHECK", "PRE_AUTH"])
    @DisplayName("Returns true for supported types when all conditions are met")
    fun returnTrueForSupportedTypesWhenAllConditionsMet(type: TransactionType) {
        every { isDependencyPresent(RAVELIN_ENCRYPT_CLASS_NAME) } returns true
        every { judo.recommendationConfiguration } returns mockk(relaxed = true)

        assertTrue(sut.isRecommendationFeatureAvailable(type))
    }
}
