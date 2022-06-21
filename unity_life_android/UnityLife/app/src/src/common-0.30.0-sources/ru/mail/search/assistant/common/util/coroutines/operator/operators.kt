package ru.mail.search.assistant.common.util.coroutines.operator

import android.os.SystemClock
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Flow, which emits countdown of given [time], every certain time [step].
 * Android implementation only.
 */
fun timerFlow(time: Long, step: Long = 1000): Flow<Long> = flow {
    val startTime = SystemClock.elapsedRealtime()
    val endTime = startTime + time
    var nextEmitTime = startTime
    var counter = endTime - startTime
    while (counter > 0) {
        val currentTime = SystemClock.elapsedRealtime()
        emit(counter)
        nextEmitTime += step
        counter -= step
        delay(nextEmitTime - currentTime)
    }
    emit(0L)
}

/**
 * Usage inside a FlowCollector to implement flow interruption with [FlowInterruptionException].
 */
@OptIn(InternalCoroutinesApi::class)
suspend inline fun <T> Flow<T>.collectWithInterruption(crossinline action: suspend (value: T) -> Unit) {
    try {
        collect(object : FlowCollector<T> {
            override suspend fun emit(value: T) = action(value)
        })
    } catch (error: FlowInterruptionException) {
        // Flow interrupted
    }
}

/**
 * Flow analog of ReactiveX Throttle First.
 * http://reactivex.io/documentation/operators/sample.html
 */
fun <T> Flow<T>.throttleFirst(time: Long): Flow<T> {
    return flow {
        var nextEmitTime = -1L
        collect { value ->
            val currentTime = SystemClock.elapsedRealtime()
            if (nextEmitTime <= currentTime) {
                emit(value)
                nextEmitTime = currentTime + time
            }
        }
    }
}