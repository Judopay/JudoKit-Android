package com.judopay

import com.judopay.model.PaymentWidgetType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing the Judo configuration object builder logic")
internal class JudoBuilderTest {

    lateinit var judoBuilder: Judo.Builder

    @BeforeEach
    fun setUp() {
        judoBuilder = Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
    }

    @Test
    @DisplayName("When no required parameters specified, build() should throw a IllegalArgumentException")
    fun testThatBuildThrows() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.build()
        }
    }
}
