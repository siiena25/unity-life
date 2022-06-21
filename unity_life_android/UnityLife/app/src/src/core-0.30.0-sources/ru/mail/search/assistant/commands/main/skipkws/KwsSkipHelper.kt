package ru.mail.search.assistant.commands.main.skipkws

import android.os.SystemClock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.coroutineContext

internal class KwsSkipHelper(
    private val kwsSkipController: KwsSkipController,
    private val poolDispatcher: PoolDispatcher
) {

    private companion object {

        private const val MIN_SKIP_TIME_MS = 20L
    }

    private val coroutineJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(coroutineJob + poolDispatcher.work)

    private val state = AtomicReference(State.KWS_RESUMED)
    private val stateMutex = Mutex()

    fun onMediaResumed(eventTime: Long, position: Long, skipIntervals: List<KwsSkipInterval>) {
        cancelTasks()
        coroutineScope.launch {
            executeNextInterval(position, eventTime, skipIntervals)
        }
    }

    fun onMediaPaused() {
        cancelTasks()
        coroutineScope.launch {
            resumeKws()
        }
    }

    fun release() {
        coroutineJob.cancel()
        GlobalScope.launch(poolDispatcher.work) {
            releaseState()
        }
    }

    private fun cancelTasks() {
        coroutineJob.cancelChildren()
    }

    private suspend fun resumeKws() {
        stateMutex.withLock {
            if (!coroutineContext.isActive) return
            if (state.compareAndSet(State.KWS_PAUSED, State.KWS_RESUMED)) {
                kwsSkipController.resume()
            }
        }
    }

    private suspend fun pauseKws() {
        stateMutex.withLock {
            if (!coroutineContext.isActive) return
            if (state.compareAndSet(State.KWS_RESUMED, State.KWS_PAUSED)) {
                kwsSkipController.pause()
            }
        }
    }

    private suspend fun releaseState() {
        stateMutex.withLock {
            val previousState = state.getAndSet(State.RELEASED)
            if (previousState == State.KWS_PAUSED) {
                kwsSkipController.resume()
            }
        }
    }

    private suspend fun executeNextInterval(
        position: Long,
        positionTime: Long,
        intervals: List<KwsSkipInterval>
    ) {
        var currentPosition = getCurrentPosition(position, positionTime)
        while (true) {
            val interval = getNextInterval(currentPosition, intervals)
            if (interval != null) {
                if (currentPosition < interval.start) {
                    resumeKws()
                    delay(interval.start - currentPosition)
                    currentPosition = getCurrentPosition(position, positionTime)
                }
                if (interval.isUnlimited()) {
                    pauseKws()
                    return
                } else if (currentPosition < interval.end) {
                    pauseKws()
                    delay(interval.end - currentPosition)
                    currentPosition = getCurrentPosition(position, positionTime)
                }
            } else {
                resumeKws()
                return
            }
        }
    }

    private fun getNextInterval(
        position: Long,
        intervals: List<KwsSkipInterval>
    ): KwsSkipInterval? {
        return intervals.firstOrNull { interval ->
            position < interval.end - MIN_SKIP_TIME_MS || interval.isUnlimited()
        }
    }

    private fun getCurrentPosition(position: Long, positionTime: Long): Long {
        return position + getCurrentTime() - positionTime
    }

    private fun getCurrentTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    private enum class State {

        KWS_RESUMED, KWS_PAUSED, RELEASED
    }
}