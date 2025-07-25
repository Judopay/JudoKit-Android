package com.judokit.android.examples.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultItem(
    val title: String,
    val value: String,
    val subResult: Result? = null,
) : Parcelable {
    override fun toString(): String = "ResultItem(title='$title', value='$value', subResult=$subResult)"
}

@Parcelize
data class Result(
    val title: String,
    val items: List<ResultItem>,
) : Parcelable {
    override fun toString(): String = "Result(title='$title', items=$items)"
}
