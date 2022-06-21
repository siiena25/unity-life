package ru.mail.search.assistant.services.music

import android.os.SystemClock
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

internal class VolumeManager(private val mediaSessionConnector: MediaSessionConnector) {

    companion object {

        private const val DUCKED_VOLUME = 0f
        private const val FADE_DURATION_NORMAL = 300L
        private const val FADE_DURATION_FAST = 100L
        private const val VOLUME_TICKER_TIME_DELTA_MS = 10L
        private const val USER_VOLUME_MULTIPLIER = 100f

        fun getUserVolume(player: Player): Int {
            return toUserVolume(player.volume)
        }

        @IntRange(from = 0, to = 100)
        private fun toUserVolume(volume: Float): Int {
            return (volume * USER_VOLUME_MULTIPLIER).toInt()
        }

        @FloatRange(from = 0.0, to = 1.0)
        private fun toPlayerVolume(volume: Int): Float {
            return volume / USER_VOLUME_MULTIPLIER
        }
    }

    private var originalVolume: Int = -1
    private var isVolumeDucked: Boolean = false
    private var volumeTicker: Job? = null

    fun getUserVolume(): Int {
        return originalVolume
    }

    fun setUserVolume(player: Player, volume: Int) {
        originalVolume = volume
        mediaSessionConnector.invalidateMediaSessionMetadata()
        if (!isVolumeDucked) {
            setVolume(player, toPlayerVolume(volume), FADE_DURATION_NORMAL)
        }
    }

    fun duckVolume(player: Player) {
        if (isVolumeDucked) return
        isVolumeDucked = true
        if (originalVolume < 0) {
            originalVolume = getUserVolume(player)
        }
        setVolume(player, DUCKED_VOLUME, FADE_DURATION_FAST)
    }

    fun unduckVolume(player: Player) {
        if (!isVolumeDucked) return
        isVolumeDucked = false
        setVolume(player, toPlayerVolume(originalVolume), FADE_DURATION_NORMAL)
    }

    private fun setVolume(player: Player, volume: Float, fadeDuration: Long) {
        volumeTicker?.cancel()
        volumeTicker = null

        val currentVolume = player.volume
        if (fadeDuration > 0 && player.playbackState == Player.STATE_READY) {
            val delta =
                (volume - currentVolume) / (fadeDuration / VOLUME_TICKER_TIME_DELTA_MS.toFloat())
            if (delta != 0f) {
                volumeTicker = GlobalScope.launch(Dispatchers.Main) {
                    runCatching { fadeVolume(player, delta, volume) }
                        .onFailure { error ->
                            if (error !is CancellationException) {
                                setVolumeInternal(player, volume)
                            }
                        }
                }
            } else {
                setVolumeInternal(player, volume)
            }
        } else {
            setVolumeInternal(player, volume)
        }
    }

    private suspend fun fadeVolume(
        player: Player,
        delta: Float,
        targetVolume: Float
    ) {
        volumeElevator(
            player.volume,
            targetVolume,
            VOLUME_TICKER_TIME_DELTA_MS,
            delta
        ).collect { volume ->
            setVolumeInternal(player, volume)
        }
        volumeTicker = null
    }

    private fun volumeElevator(
        currentVolume: Float,
        targetVolume: Float,
        timeDelta: Long,
        step: Float
    ): Flow<Float> {
        return flow {
            var nextVolume = currentVolume + step
            var nextTime = SystemClock.elapsedRealtime() + timeDelta
            while (nextVolume < targetVolume) {
                emit(nextVolume)
                val currentTime = SystemClock.elapsedRealtime()
                val delayTime = (nextTime - currentTime).coerceAtLeast(0)
                delay(delayTime)
                nextVolume += step
                nextTime += timeDelta
            }
            emit(targetVolume)
        }
    }

    private fun setVolumeInternal(player: Player, volume: Float) {
        player.volume = volume
    }
}