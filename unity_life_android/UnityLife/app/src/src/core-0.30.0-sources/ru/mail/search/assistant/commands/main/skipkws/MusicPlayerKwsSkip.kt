package ru.mail.search.assistant.commands.main.skipkws

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.MainThread
import kotlinx.coroutines.*
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import ru.mail.search.assistant.services.music.extension.messageId
import ru.mail.search.assistant.services.music.extension.trackIndex

internal class MusicPlayerKwsSkip(
    kwsSkipController: KwsSkipController,
    private val kwsSkipDataRepository: MusicKwsSkipRepository,
    private val poolDispatcher: PoolDispatcher
) {

    private val kwsSkipHelper = KwsSkipHelper(kwsSkipController, poolDispatcher)

    private val coroutineContext = SupervisorJob() + poolDispatcher.main
    private val coroutineScope = CoroutineScope(coroutineContext)

    private var metadata: MediaMetadataCompat? = null
    private var playbackState: PlaybackStateCompat? = null

    private var isReleased = false

    @MainThread
    fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        if (isReleased) return
        this.metadata = metadata
        coroutineContext.cancelChildren()
        coroutineScope.launch(poolDispatcher.main.immediate) {
            refreshState(playbackState, metadata)
        }
    }

    @MainThread
    fun onPlaybackStateChanged(playbackState: PlaybackStateCompat?) {
        if (isReleased) return
        this.playbackState = playbackState
        coroutineContext.cancelChildren()
        coroutineScope.launch(poolDispatcher.main.immediate) {
            refreshState(playbackState, metadata)
        }
    }

    @MainThread
    fun release() {
        if (isReleased) return
        isReleased = true
        coroutineContext.cancel()
        kwsSkipHelper.release()
    }

    private suspend fun refreshState(
        playbackState: PlaybackStateCompat?,
        metadata: MediaMetadataCompat?
    ) {
        if (playbackState != null &&
            playbackState.state == PlaybackStateCompat.STATE_PLAYING &&
            metadata != null
        ) {
            val time = playbackState.lastPositionUpdateTime.takeIf { it > 0 }
            if (time != null) {
                val position = playbackState.position
                val skipIntervals = metadata.getKwsSkipIntervals()
                if (skipIntervals != null) {
                    kwsSkipHelper.onMediaResumed(time, position, skipIntervals)
                    return
                }
            }
        }
        kwsSkipHelper.onMediaPaused()
    }

    private suspend fun MediaMetadataCompat.getKwsSkipIntervals(): List<KwsSkipInterval>? {
        val messageId = messageId ?: return null
        val trackIndex = trackIndex
        return kwsSkipDataRepository.getSkipKwsIntervals(messageId, trackIndex)
    }
}