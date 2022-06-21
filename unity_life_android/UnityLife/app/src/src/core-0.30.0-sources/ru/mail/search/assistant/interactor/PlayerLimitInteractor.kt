package ru.mail.search.assistant.interactor

import android.os.SystemClock
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.*
import ru.mail.search.assistant.BuildConfig
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.PlayerLimitRepository
import ru.mail.search.assistant.entities.PlayerLimit
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError

import java.util.*

typealias LimitFinishCallback = () -> Unit

class PlayerLimitInteractor(
    private val playerLimitRepository: PlayerLimitRepository,
    private val analytics: Analytics?,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    companion object {

        private const val TAG = "PlayerLimit"
        private const val COUNTER_DELAY_MS = 5000L
    }

    private var isAppBackground = false
    private var isMusicPlaying = false
    private var limitFinishCallback: LimitFinishCallback? = null

    private var isCounterActual = false
    private val limit
        get() = devMenuLimit ?: suggestedLimit
    private var suggestedLimit = -1L
    var devMenuLimit: Long?
        get() = playerLimitRepository.devMenuLimit
        set(value) {
            playerLimitRepository.devMenuLimit = value
        }
    private var timeSpent = 0L
    private var refreshTime = 0L

    private var counterJob: Job? = null

    @MainThread
    fun isPlayingAvailable(): Boolean {
        return !isAppBackground || checkAndLaunchRefreshTimeCount()
    }

    @WorkerThread
    suspend fun prepareLimits() {
        checkAndRefreshTimeCount()
    }

    @MainThread
    fun onStartPlaying(callback: LimitFinishCallback) {
        if (isMusicPlaying) return
        isMusicPlaying = true
        limitFinishCallback = callback
        checkState()
    }

    @MainThread
    fun onStopPlaying() {
        if (!isMusicPlaying) return
        isMusicPlaying = false
        limitFinishCallback = null
        checkState()
    }

    @MainThread
    fun onAppForeground() {
        if (!isAppBackground) return
        isAppBackground = false
        checkState()
    }

    @MainThread
    fun onAppBackground() {
        if (isAppBackground) return
        isAppBackground = true
        checkState()
    }

    @MainThread
    fun setLimit(limit: Long) {
        suggestedLimit = limit
    }

    private fun checkState() {
        val startCounter = isAppBackground && isMusicPlaying
        if (startCounter) {
            counterJob?.cancel()
            counterJob = GlobalScope.launch(poolDispatcher.main) {
                if (checkAndRefreshTimeCount()) {
                    logLimit { "Start player limit counter: limit (ms) - $limit, spent (ms) - $timeSpent" }
                    launchTimeCounter()
                } else {
                    logLimit { "Player limit is reached: limit (ms) - $limit, spent (ms) - $timeSpent" }
                    limitFinishCallback?.invoke()
                }
            }
        } else {
            logLimit { "Stop player limit counter: limit (ms) - $limit, spent (ms) - $timeSpent" }
            counterJob?.cancel()
            counterJob = null
        }
    }

    /**
     * Check balance and launch refresh
     *
     * @return time balance is available
     */
    private fun checkAndLaunchRefreshTimeCount(): Boolean {
        return if (isCounterActual) {
            return checkTimeCount()
        } else {
            GlobalScope.launch(poolDispatcher.main) { refreshTimeCount() }
            true
        }
    }

    /**
     * Check time counter and immediate refresh
     *
     * @return time balance is available
     */
    private suspend fun checkAndRefreshTimeCount(): Boolean {
        return if (isCounterActual) {
            return checkTimeCount()
        } else {
            refreshTimeCount()
            true
        }
    }

    private fun checkTimeCount(): Boolean {
        if (System.currentTimeMillis() > refreshTime) {
            refreshTime = getNextRefreshTime()
            timeSpent = 0
        }
        return timeSpent < limit || limit < 0
    }

    private suspend fun refreshTimeCount() {
        if (isCounterActual) return
        val limit = getStoredLimit()
            ?.takeIf { it.refreshTime > System.currentTimeMillis() }
            ?: PlayerLimit(0, getNextRefreshTime())
        if (!isCounterActual) {
            isCounterActual = true
            timeSpent = limit.timeSpent
            refreshTime = limit.refreshTime
        }
    }

    private fun getNextRefreshTime(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private suspend fun launchTimeCounter() {
        var lastUpdate = SystemClock.elapsedRealtime()
        while (true) {
            delay(COUNTER_DELAY_MS)
            val currentTime = SystemClock.elapsedRealtime()
            val delta = currentTime - lastUpdate
            lastUpdate = currentTime
            if (!decrementAndCheckTimeCount(delta)) {
                logLimit { "Player limit is reached: limit (ms) - $limit, spent (ms) - $timeSpent" }
                limitFinishCallback?.invoke()
                return
            }
        }
    }

    private suspend fun decrementAndCheckTimeCount(value: Long): Boolean {
        timeSpent += value
        storeLimit()
        logLimit { "Player limit counter decremented: limit (ms) - $limit, spent (ms) - $timeSpent" }
        return checkTimeCount()
    }

    private suspend fun storeLimit() {
        val limit = PlayerLimit(timeSpent, refreshTime)
        storeLimit(limit)
    }

    private suspend fun getStoredLimit(): PlayerLimit? = withContext(poolDispatcher.io) {
        runCatching { playerLimitRepository.getLimit() }
            .onFailure { error ->
                logger?.e(TAG, error, "Error while accessing stored music limit")
                analytics?.log(MediaPlayerError("Error while accessing stored music limit"))
            }
            .getOrNull()
    }

    private suspend fun storeLimit(limit: PlayerLimit) {
        withContext(poolDispatcher.io) {
            runCatching { playerLimitRepository.saveLimit(limit) }
                .onFailure { error ->
                    logger?.e(TAG, error, "Error while storing music limit")
                    analytics?.log(MediaPlayerError("Error while storing music limit"))
                }
        }
    }

    private inline fun logLimit(block: () -> String) {
        if (BuildConfig.DEBUG) {
            logger?.d(TAG, block())
        }
    }
}