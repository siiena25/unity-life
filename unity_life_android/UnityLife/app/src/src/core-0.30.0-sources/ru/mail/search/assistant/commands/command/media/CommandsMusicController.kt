package ru.mail.search.assistant.commands.command.media

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.MainThread
import ru.mail.search.assistant.api.phrase.PlayerData
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatTrigger
import ru.mail.search.assistant.commands.main.skipkws.KwsSkipController
import ru.mail.search.assistant.commands.main.skipkws.MusicKwsSkipRepository
import ru.mail.search.assistant.commands.main.skipkws.MusicPlayerKwsSkip
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.TimeUtils
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.PlayerEventRepository
import ru.mail.search.assistant.data.exception.MediaPlayerConnectException
import ru.mail.search.assistant.services.music.MusicPlayerService
import ru.mail.search.assistant.services.music.extension.*
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError

internal class CommandsMusicController(
    private val context: Context,
    private val kwsSkipController: KwsSkipController,
    private val kwsSkipRepository: MusicKwsSkipRepository,
    private val analytics: Analytics?,
    private val poolDispatcher: PoolDispatcher,
    private val playerEventRepository: PlayerEventRepository,
    private val logger: Logger?
) {

    private val controllerCallback = ControllerCallback()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var musicPlayerKwsSkip: MusicPlayerKwsSkip? = null
    private var mediaBrowser: MediaBrowserCompat? = null
    private var isConnected = false
    private var controller: MediaControllerCompat? = null
    private var metadata: MediaMetadataCompat? = null
    private var playbackState: PlaybackStateCompat? = null

    private val transportControls get() = controller?.transportControls

    fun connect() = mainThread {
        if (isConnected) return@mainThread
        isConnected = true
        disconnectImmediate()
        musicPlayerKwsSkip =
            MusicPlayerKwsSkip(kwsSkipController, kwsSkipRepository, poolDispatcher)
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, MusicPlayerService::class.java),
            ConnectionCallback(),
            null
        ).apply { connect() }
    }

    fun disconnect() = mainThread {
        if (!isConnected) return@mainThread
        isConnected = false
        musicPlayerKwsSkip?.release()
        musicPlayerKwsSkip = null
        disconnectImmediate()
    }

    fun play(messageId: Long) = mainThread {
        val controls = transportControls ?: return@mainThread
        if (isCurrentlyPlaying(messageId)) {
            controls.play()
            playerEventRepository.onTrackResume(
                PlayerDeviceStatTrigger.BACKEND_CONTROLS,
                controller
            )
        } else {
            playerEventRepository.onTrackStart(
                PlayerDeviceStatTrigger.BACKEND_NEW_MEDIA,
                controller
            )
            controls.playFromMediaId(messageId.toString(), null)
        }
    }

    fun play(
        messageId: Long,
        autoPlay: Boolean,
        trackNumber: Int,
        trackPosition: Float
    ) = mainThread {
        val controls = transportControls ?: return@mainThread
        val position = TimeUtils.secondToMillis(trackPosition)
        playerEventRepository.onTrackStart(
            PlayerDeviceStatTrigger.BACKEND_NEW_MEDIA,
            controller
        )
        if (autoPlay) {
            controls.playFromMediaId(messageId.toString(), trackNumber, position)
        } else {
            controls.prepareFromMediaId(messageId.toString(), trackNumber, position)
        }
    }

    fun pause() = mainThread {
        transportControls?.pause()
        playerEventRepository.onTrackPause(
            PlayerDeviceStatTrigger.BACKEND_CONTROLS,
            controller
        )
    }

    fun stop() = mainThread {
        playerEventRepository.onTrackStop(
            PlayerDeviceStatTrigger.BACKEND_CONTROLS,
            controller
        )
        transportControls?.rewind(0, 0)
        transportControls?.stop()
    }

    fun skipToNext() = mainThread {
        transportControls?.skipToNext()
    }

    fun skipToPrevious() = mainThread {
        transportControls?.skipToPreviousDefinitely()
    }

    fun fastForward(time: Long) = mainThread {
        transportControls?.fastForward(time)
    }

    fun fastBackward(time: Long) = mainThread {
        transportControls?.fastBackward(time)
    }

    fun setVolume(volume: Int) = mainThread {
        transportControls?.setUserVolume(volume)
    }

    fun duckVolume() = mainThread {
        transportControls?.duckVolume()
    }

    fun unduckVolume() = mainThread {
        transportControls?.unduckVolume()
    }

    fun setRepeatMode(mode: RepeatMode, count: Int) = mainThread {
        val modeConst = when (mode) {
            RepeatMode.NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
            RepeatMode.TRACK -> PlaybackStateCompat.REPEAT_MODE_ONE
            RepeatMode.PLAYLIST -> PlaybackStateCompat.REPEAT_MODE_ALL
        }
        transportControls?.setRepeatMode(modeConst)
    }

    fun rewind(trackNumber: Int, trackPosition: Float) = mainThread {
        val position = TimeUtils.secondToMillis(trackPosition)
        transportControls?.rewind(trackNumber, position)
    }

    @MainThread
    fun getPlayedMessageId(): Long? {
        return metadata?.messageId
    }

    @MainThread
    fun getPlayerData(): PlayerData? {
        val metadata = metadata ?: return null
        val playbackState = playbackState ?: return null
        val isPlaying = when {
            playbackState.isPlaying -> true
            playbackState.isPrepared -> false
            else -> return null
        }
        val audioSource = metadata.audioSource
        val volume = metadata.userVolume
        val duration = metadata.duration
        val elapsed = playbackState.currentPlayBackPosition
        val isUnlimited = duration == null
        return PlayerData(
            trackIndex = metadata.trackIndex,
            isPlaying = isPlaying,
            trackDurationMs = duration,
            trackPositionMs = if (!isUnlimited) elapsed else null,
            volume = volume,
            source = audioSource?.source
        )
    }

    private fun isCurrentlyPlaying(messageId: Long): Boolean {
        return metadata?.messageId == messageId && playbackState?.isPrepared == true
    }

    private fun attachController(controller: MediaControllerCompat) {
        controller.registerCallback(controllerCallback, mainThreadHandler)
        updateMetadata(controller.metadata)
        updatePlaybackState(controller.playbackState)
        this.controller = controller
    }

    private fun disconnectImmediate() {
        detachController()
        detachMediaBrowser()
    }

    private fun detachController() {
        controller?.unregisterCallback(controllerCallback)
        controller = null
    }

    private fun detachMediaBrowser() {
        mediaBrowser?.disconnect()
        mediaBrowser = null
    }

    private fun updateMetadata(metadata: MediaMetadataCompat?) {
        this.metadata = metadata
        musicPlayerKwsSkip?.onMetadataChanged(metadata)
    }

    private fun updatePlaybackState(state: PlaybackStateCompat?) {
        this.playbackState = state
        musicPlayerKwsSkip?.onPlaybackStateChanged(state)
    }

    private inline fun mainThread(crossinline block: () -> Unit) {
        mainThreadHandler.post { block() }
    }

    inner class ConnectionCallback : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaBrowser?.let { mediaBrowser ->
                attachController(MediaControllerCompat(context, mediaBrowser.sessionToken))
            }
        }

        override fun onConnectionFailed() {
            analytics?.log(MediaPlayerError("Commands media browser connection failed"))
            logger?.e(
                Tag.ASSISTANT_COMMAND,
                MediaPlayerConnectException("Commands media browser connection failed")
            )
        }
    }

    inner class ControllerCallback : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            updateMetadata(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updatePlaybackState(state)
        }
    }
}