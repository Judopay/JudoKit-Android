package com.judopay.judokit.android

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Starts collecting [flow] in the [TestScope.backgroundScope] using an [UnconfinedTestDispatcher]
 * and returns the mutable list that accumulates all emitted values.
 *
 * Must be called inside a [kotlinx.coroutines.test.runTest] block.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> TestScope.collectFlow(flow: Flow<T>): MutableList<T> {
    val results = mutableListOf<T>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        flow.collect(results::add)
    }
    return results
}
