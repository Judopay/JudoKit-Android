package com.judopay.judokit.android.api.error

/**
 * An exception thrown if the Judo credentials have not been initialized correctly.
 */
class TokenSecretError(
    detailMessage: String?,
) : Error(detailMessage)
