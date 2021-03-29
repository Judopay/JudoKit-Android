package com.judopay.judokit.android.api.error

class DuplicateTransactionError(ref: String) : Error("Cannot make duplicate transaction with same unique ref: $ref")
