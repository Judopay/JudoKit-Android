package com.judokit.android.examples.common

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.judokit.android.examples.model.Result
import com.judokit.android.examples.model.ResultItem
import com.judokit.android.examples.result.RESULT
import com.judokit.android.examples.result.ResultActivity
import java.math.BigDecimal
import java.util.Date
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}

fun Activity.startResultActivity(result: Result) {
    val intent = Intent(this, ResultActivity::class.java)
    intent.putExtra(RESULT, result)
    startActivity(intent)
}

fun Any.toResult(): Result {
    val title = this::class.simpleName ?: "Details"
    val items = this::class.memberProperties.map { it.toResultItem(this) }
    return Result(title, items)
}

fun <T : Any> KProperty1<T, *>.toResultItem(classInstance: Any?): ResultItem {
    var subResult: Result? = null
    var propValue = ""

    fun getPropValuePlaceholder(value: Any?) = if (value != null) {
        "(0 elements)"
    } else {
        "null"
    }

    when (returnType.classifier) {
        String::class,
        Date::class,
        Number::class,
        BigDecimal::class,
        Int::class,
        Float::class,
        Double::class,
        Boolean::class -> {
            propValue = getter.call(classInstance)?.toString() ?: "null"
        }

        List::class -> {
            val value = getter.call(classInstance) as? List<*>
            val propName = "$name (${value?.size ?: 0} elements)"
            val items = value?.toResultItemList(propName)

            if (items.isNullOrEmpty()) {
                propValue = getPropValuePlaceholder(value)
            } else {
                subResult = Result(propName, items)
            }
        }

        Map::class -> {
            val value = getter.call(classInstance) as? Map<*, *>
            val items = value?.toResultItemList(name)

            if (items.isNullOrEmpty()) {
                propValue = getPropValuePlaceholder(value)
            } else {
                subResult = Result(name, items)
            }
        }

        // complex objects,
        // needs to be extracted in a separate `Result` wrapper
        else -> {
            subResult = getter.call(classInstance)?.toResult()
        }
    }

    return ResultItem(name, propValue, subResult)
}

fun List<*>.toResultItemList(propName: String): List<ResultItem> {
    val items = mutableListOf<ResultItem>()

    for (item in this) {
        val myName = "$propName ${indexOf(item)}"
        items.add(ResultItem(myName, "", item?.toResult()))
    }

    return items
}

fun Map<*, *>.toResultItemList(propName: String): List<ResultItem> {
    val items = mutableListOf<ResultItem>()

    for (item in this.entries) {
        val key = item.key as? String ?: ""
        val value = item.value as? String ?: ""
        items.add(ResultItem(key, value))
    }

    return items
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> {
        @Suppress("deprecation")
        getParcelable(key) as? T
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}
