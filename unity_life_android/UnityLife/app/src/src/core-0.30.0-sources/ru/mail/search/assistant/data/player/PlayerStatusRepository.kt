package ru.mail.search.assistant.data.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.coroutines.*
import ru.mail.search.assistant.api.statistics.playerstatus.PlayerStatus
import ru.mail.search.assistant.api.statistics.playerstatus.PlayerStatusDataSource
import ru.mail.search.assistant.api.statistics.playerstatus.PlayerStatusState
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.services.music.callback.BaseMediaControllerCallback
import ru.mail.search.assistant.services.music.extension.*
import ru.mail.search.assistant.util.Tag
import java.util.concurrent.TimeUnit

internal class PlayerStatusRepository(
    private val sessionProvider: SessionCredentialsProvider,
    private val remote: PlayerStatusDataSource,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private companion object {

        private val STATUS_SENT_PERIOD = TimeUnit.SECONDS.toMillis(5)
    }

    private var currentMetadata: MediaMetadataCompat? = null
    private var trackSelectionTime: Long? = null
    private var lastElapsed: Long? = null

    private fun onPlayerEvent(
        state: PlayerStatusState,
        controller: MediaControllerCompat,
        metadata: MediaMetadataCompat
    ) {
        val elapsed = controller.playbackState.currentPlayBackPosition
        sendPlayerEvent(state, metadata, controller, elapsed)
    }

    private fun sendPlayerEvent(
        state: PlayerStatusState,
        metadata: MediaMetadataCompat,
        controller: MediaControllerCompat,
        elapsed: Long?
    ) {
        val volume = controller.playbackInfo.currentVolume
        val repeat = controller.repeatMode > 0
        val trackSelectionTime = trackSelectionTime ?: currentTimeMillis()
        val playerStatus = PlayerStatus(
            title = metadata.title,
            mediaUrl = metadata.mediaUri,
            state = state,
            trackIndex = metadata.trackIndex,
            trackDurationMs = metadata.duration,
            trackPositionMs = elapsed,
            timestampMs = currentTimeMillis(),
            volume = volume,
            isPlaylistRepeatEnabled = repeat,
            trackSelectionTimeMs = trackSelectionTime,
            isPlaylistShuffleEnabled = null,
            source = metadata.audioSource?.source
        )

        lastElapsed = elapsed
        GlobalScope.launch(poolDispatcher.io) {
            runCatching {
                remote.sendStatus(sessionProvider.getCredentials(), playerStatus)
            }.onFailure { error ->
                if (!error.isCausedByPoorNetworkConnection() && error !is CancellationException) {
                    logger?.e(Tag.PLAYER_STATUS, error, "Error while sending player status events")
                }
            }
        }
    }

    private fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    private fun onTrackChanged(controller: MediaControllerCompat) {
        val oldMetadata = currentMetadata
        val newMetadata = getCurrentMetadata(controller) ?: return
        trackSelectionTime = currentTimeMillis()
        if (oldMetadata != null && oldMetadata.mediaId != newMetadata.mediaId) {
            sendPlayerEvent(
                PlayerStatusState.STOP,
                oldMetadata,
                controller,
                lastElapsed
            )
            sendPlayerEvent(
                PlayerStatusState.PLAY,
                newMetadata,
                controller,
                controller.playbackState.currentPlayBackPosition
            )
        }
    }

    private fun getCurrentMetadata(mediaController: MediaControllerCompat): MediaMetadataCompat? {
        return mediaController.metadata
            ?.takeIf { metadata -> metadata.mediaId != null }
            ?.also { metadata -> currentMetadata = metadata }
    }

    private fun getAndResetLastMetadata(): MediaMetadataCompat? {
        return currentMetadata
            ?.also { currentMetadata = null }
    }

    internal inner class PlayerStatusCallback(
        private val mediaController: MediaControllerCompat
    ) : BaseMediaControllerCallback() {

        private var job: Job? = null

        override fun onPlay() {
            job?.cancel()
            job = GlobalScope.launch(poolDispatcher.main) {
                sendEventsWhilePlaying()
            }
        }

        override fun onPause() {
            job?.cancel()
            val metadata = getCurrentMetadata(mediaController) ?: return
            onPlayerEvent(PlayerStatusState.PAUSE, mediaController, metadata)
        }

        override fun onStop() {
            job?.cancel()
            val metadata = getAndResetLastMetadata() ?: return
            onPlayerEvent(PlayerStatusState.STOP, mediaController, metadata)
        }

        private suspend fun sendEventsWhilePlaying() {
            while (true) {
                getCurrentMetadata(mediaController)?.let { metadata ->
                    onPlayerEvent(PlayerStatusState.PLAY, mediaController, metadata)
                }
                delay(STATUS_SENT_PERIOD)
            }
        }
    }

    inner class PlayerStatusTrackChangedCallback(
        private val mediaController: MediaControllerCompat
    ) : AnalyticsListener {

        override fun onTracksChanged(
            eventTime: AnalyticsListener.EventTime,
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            onTrackChanged(mediaController)
        }
    }
}