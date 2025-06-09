package com.judopay.judokit.android.api.model.response

/**
 * Represents the list of [Receipt] objects returned when querying the judo API for Receipts.
 */
class Receipts(
    val resultCount: Int,
    val pageSize: Int,
    val offset: Int,
    val results: List<Receipt>,
) {
    override fun toString(): String = "Receipts(resultCount=$resultCount, pageSize=$pageSize, offset=$offset, results=$results)"
}
