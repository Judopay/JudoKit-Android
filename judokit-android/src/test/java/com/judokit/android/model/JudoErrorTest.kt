package com.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoError static methods")
internal class JudoErrorTest {

    @DisplayName("Given userCancelled is called, then return default cancel error")
    @Test
    fun returnDefaultCancelErrorOnUserCancelled() {
        assertEquals(USER_CANCELLED, JudoError.userCancelled().code)
        assertEquals(USER_CANCELLED_MSG, JudoError.userCancelled().message)
    }

    @DisplayName("Given generic is called, then return default generic error")
    @Test
    fun returnDefaultGenericErrorOnGenericCall() {
        assertEquals(INTERNAL_ERROR, JudoError.generic().code)
        assertEquals(UNKNOWN_ERROR_MSG, JudoError.generic().message)
    }
}
