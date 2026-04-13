package com.judopay.judokit.android.api.deserializer

import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.NotSerializableException

@DisplayName("Testing EnumSerializers")
internal class EnumSerializersTest {
    private val context: JsonSerializationContext = mockk()

    @Test
    @DisplayName("Given ScaExemption LOW_VALUE, then it serializes to the correct value string")
    fun serializeScaExemptionLowValue() {
        val serializer = ScaExemptionSerializer()
        every { context.serialize("lowValue") } returns JsonPrimitive("lowValue")
        val result = serializer.serialize(ScaExemption.LOW_VALUE, null, context)
        assertEquals(JsonPrimitive("lowValue"), result)
    }

    @Test
    @DisplayName("Given ScaExemption TRANSACTION_RISK_ANALYSIS, then it serializes to the correct value string")
    fun serializeScaExemptionTransactionRiskAnalysis() {
        val serializer = ScaExemptionSerializer()
        every { context.serialize("transactionRiskAnalysis") } returns JsonPrimitive("transactionRiskAnalysis")
        val result = serializer.serialize(ScaExemption.TRANSACTION_RISK_ANALYSIS, null, context)
        assertEquals(JsonPrimitive("transactionRiskAnalysis"), result)
    }

    @Test
    @DisplayName("Given ScaExemptionSerializer with null context, then it throws NotSerializableException")
    fun serializeScaExemptionWithNullContext() {
        val serializer = ScaExemptionSerializer()
        assertThrows<NotSerializableException> {
            serializer.serialize(ScaExemption.LOW_VALUE, null, null)
        }
    }

    @Test
    @DisplayName("Given ChallengeRequestIndicator NO_PREFERENCE, then it serializes to the correct value string")
    fun serializeChallengeRequestIndicatorNoPreference() {
        val serializer = ChallengeRequestIndicatorSerializer()
        every { context.serialize("noPreference") } returns JsonPrimitive("noPreference")
        val result = serializer.serialize(ChallengeRequestIndicator.NO_PREFERENCE, null, context)
        assertEquals(JsonPrimitive("noPreference"), result)
    }

    @Test
    @DisplayName("Given ChallengeRequestIndicator CHALLENGE_AS_MANDATE, then it serializes to the correct value string")
    fun serializeChallengeRequestIndicatorChallengeAsMandate() {
        val serializer = ChallengeRequestIndicatorSerializer()
        every { context.serialize("challengeAsMandate") } returns JsonPrimitive("challengeAsMandate")
        val result = serializer.serialize(ChallengeRequestIndicator.CHALLENGE_AS_MANDATE, null, context)
        assertEquals(JsonPrimitive("challengeAsMandate"), result)
    }

    @Test
    @DisplayName("Given ChallengeRequestIndicatorSerializer with null context, then it throws NotSerializableException")
    fun serializeChallengeRequestIndicatorWithNullContext() {
        val serializer = ChallengeRequestIndicatorSerializer()
        assertThrows<NotSerializableException> {
            serializer.serialize(ChallengeRequestIndicator.NO_PREFERENCE, null, null)
        }
    }
}
