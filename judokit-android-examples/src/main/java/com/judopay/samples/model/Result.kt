package com.judopay.samples.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultItem(
    val title: String,
    val value: String,
    val subResult: Result?
) : Parcelable {
    override fun toString(): String {
        return "ResultItem(title='$title', value='$value', subResult=$subResult)"
    }
}

@Parcelize
data class Result(
    val title: String,
    val items: List<ResultItem>
) : Parcelable {
    override fun toString(): String {
        return "Result(title='$title', items=$items)"
    }
}
