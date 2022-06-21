package ru.mail.search.assistant.data

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatDataSource
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatEvent
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatEventType
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatTrigger
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.entities.audio.AudioSource
import ru.mail.search.assistant.services.music.callback.BaseMediaControllerCallback
import ru.mail.search.assistant.services.music.extension.*
import ru.mail.search.assistant.util.Tag
import java.util.concurrent.TimeUnit

class PlayerEventRepository(
    private val sessionProvider: SessionCredentialsProvider,
    private val playerDeviceStatDataSource: PlayerDeviceStatDataSource,
    private val logger: Logger?,
    private val poolDispatcher: PoolDispatcher
) {

    private companion object {

        private const val MEDIA_TYPE_RADIO = "radio"
        private const val MEDIA_TYPE_NOISE = "noise"
        private val COUNTER_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15)
    }

    private val pendingEvents =
        arrayListOf<Pair<PlayerDeviceStatEventType, PlayerDeviceStatTrigger>>()

    private var lastTrackMetadata: MediaMetadataCompat? = null
    private var lastSkipToNextOrPreviousTrigger = PlayerDeviceStatTrigger.PLAYBACK
    private var lastSkipToNextOrPreviousTrackPosition: Long? = null
    private var wasStopEventHandled = false
    private var lastEvent: PlayerDeviceStatEventType? = null

    /*
     * This is used because when we start music we gotta wait for metadata to update to
     * send correct track info.
     */
    fun onTrackStart(trigger: PlayerDeviceStatTrigger, controller: MediaControllerCompat?) {
        pendingEvents.add(PlayerDeviceStatEventType.TRACK_START to trigger)
        lastTrackMetadata?.let { metadata ->
            val position = controller?.playbackState?.position ?: 0
            sendPlayerEvent(PlayerDeviceStatEventType.TRACK_STOP, trigger, metadata, position)
        }
    }

    fun onTrackPlayPause(trigger: PlayerDeviceStatTrigger, mediaController: MediaControllerCompat) {
        if (lastEvent == null || lastEvent == PlayerDeviceStatEventType.TRACK_PAUSE) {
            onTrackResume(trigger, mediaController)
        } else {
            onTrackPause(trigger, mediaController)
        }
    }

    fun onTrackStop(trigger: PlayerDeviceStatTrigger, controller: MediaControllerCompat?) {
        onPlayerEvent(PlayerDeviceStatEventType.TRACK_STOP, trigger, controller)
        wasStopEventHandled = true
    }

    fun onTrackResume(trigger: PlayerDeviceStatTrigger, controller: MediaControllerCompat?) {
        onPlayerEvent(PlayerDeviceStatEventType.TRACK_RESUME, trigger, controller)
    }

    fun onTrackPause(trigger: PlayerDeviceStatTrigger, controller: MediaControllerCompat?) {
        onPlayerEvent(PlayerDeviceStatEventType.TRACK_PAUSE, trigger, controller)
    }

    fun onSkipToNextOrPrevious(
        trigger: PlayerDeviceStatTrigger,
        controller: MediaControllerCompat?
    ) {
        lastSkipToNextOrPreviousTrigger = trigger
        lastSkipToNextOrPreviousTrackPosition = controller?.playbackState?.position
    }

    private fun onPlayerEvent(
        eventType: PlayerDeviceStatEventType,
        trigger: PlayerDeviceStatTrigger? = null,
        controller: MediaControllerCompat?
    ) {
        controller?.metadata?.let { metadata ->
            lastTrackMetadata = if (metadata.mediaId != null) {
                sendPlayerEvent(eventType, trigger, metadata, controller.playbackState.position)
                metadata
            } else {
                null
            }
        }
    }

    private fun sendPlayerEvent(
        eventType: PlayerDeviceStatEventType,
        trigger: PlayerDeviceStatTrigger?,
        metadata: MediaMetadataCompat,
        trackPosition: Long
    ) {
        lastEvent = eventType
        GlobalScope.launch(poolDispatcher.io) {
            runCatching {
                val credentials = sessionProvider.getCredentials()
                val audioSource = metadata.audioSource
                val event = PlayerDeviceStatEvent(
                    type = eventType,
                    timestampMs = System.currentTimeMillis(),
                    mediaUrl = metadata.mediaUri.toString(),
                    trackPositionMs = mapPosition(trackPosition, eventType, audioSource),
                    trackDurationMs = metadata.duration,
                    playbackDurationMs = null,
                    trigger = trigger?.id,
                    statFlags = metadata.statFlags,
                    source = audioSource?.source
                )
                playerDeviceStatDataSource.sendEvent(credentials, event)
            }
                .onFailure { error ->
                    if (!error.isCausedByPoorNetworkConnection()) {
                        val message = "Failed to send player device stat event"
                        logger?.e(Tag.PLAYER_STATISTICS, error, message)
                    }
                }
        }
    }

    private fun mapPosition(
        trackPosition: Long,
        eventType: PlayerDeviceStatEventType,
        audioSource: AudioSource?
    ): Long? {
        if (audioSource != null && isRadioOrNoise(audioSource)) return null
        if (eventType == PlayerDeviceStatEventType.TRACK_START) return null
        return trackPosition
    }

    private fun isRadioOrNoise(audioSource: AudioSource): Boolean {
        return audioSource.sourceType == MEDIA_TYPE_RADIO || audioSource.sourceType == MEDIA_TYPE_NOISE
    }

    private fun onTrackChanged(controller: MediaControllerCompat?) {
        val newMetadata = controller?.metadata
        if (newMetadata == null || newMetadata.mediaId == null) return
        lastTrackMetadata?.let { oldMetadata ->
            if (newMetadata.mediaId != oldMetadata.mediaId) {
                sendPlayerEvent(
                    PlayerDeviceStatEventType.TRACK_STOP,
                    lastSkipToNextOrPreviousTrigger,
                    oldMetadata,
                    lastSkipToNextOrPreviousTrackPosition ?: oldMetadata.duration ?: 0
                )
                sendPlayerEvent(
                    PlayerDeviceStatEventType.TRACK_START,
                    lastSkipToNextOrPreviousTrigger,
                    newMetadata,
                    0
                )
                lastSkipToNextOrPreviousTrigger = PlayerDeviceStatTrigger.PLAYBACK
                lastSkipToNextOrPreviousTrackPosition = null
            }
        }
        lastTrackMetadata = newMetadata
    }

    private fun processPendingEvents(controller: MediaControllerCompat?) {
        if (controller?.metadata?.mediaId != null) {
            for (event in pendingEvents) {
                onPlayerEvent(event.first, event.second, controller)
            }
            pendingEvents.clear()
        }
    }

    inner class PlayerEventsCallback(
        private val mediaController: MediaControllerCompat
    ) : BaseMediaControllerCallback() {

        private var job: Job? = null

        override fun onPlay() {
            job?.cancel()
            job = GlobalScope.launch(poolDispatcher.main) {
                launchTimeCounter()
            }
        }

        override fun onPause() {
            job?.cancel()
        }

        override fun onStop() {
            job?.cancel()
            if (!wasStopEventHandled) {
                onPlayerEvent(
                    PlayerDeviceStatEventType.TRACK_STOP,
                    PlayerDeviceStatTrigger.PLAYBACK,
                    mediaController
                )
            }
            wasStopEventHandled = false
            lastTrackMetadata = null
        }

        /*
         * This callback is used because sometimes when PlayerEventRepository.onPlayerEvent() is called
         * it has not got correct metadata, so we should try to resend event after metadata is updated
         * or changed.
         */
        override fun onMetadataChanged() {
            processPendingEvents(mediaController)
        }

        private suspend fun launchTimeCounter() {
            while (true) {
                delay(COUNTER_DELAY_MILLIS)
                onPlayerEvent(
                    eventType = PlayerDeviceStatEventType.TRACK_PLAYING,
                    controller = mediaController
                )
            }
        }
    }

    inner class PlayerEventsTrackChangedCallback(
        private val mediaController: MediaControllerCompat
    ) : AnalyticsListener {

        override fun onTracksChanged(
            eventTime: AnalyticsListener.EventTime,
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            super.onTracksChanged(eventTime, trackGroups, trackSelections)
            onTrackChanged(mediaController)
        }
    }
}