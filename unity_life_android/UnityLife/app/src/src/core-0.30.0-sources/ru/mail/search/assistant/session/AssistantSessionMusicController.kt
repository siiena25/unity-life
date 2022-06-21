package ru.mail.search.assistant.session

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import ru.mail.search.assistant.commands.main.skipkws.MusicPlayerMediaFailureHandler
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.exception.MediaPlayerConnectException
import ru.mail.search.assistant.services.music.MusicPlayerService
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError

class AssistantSessionMusicController(
    private val context: Context,
    private val analytics: Analytics?,
    private val logger: Logger?,
    private val mediaFailureHandler: MusicPlayerMediaFailureHandler
) {

    private val controllerCallback = ControllerCallback()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var mediaBrowser: MediaBrowserCompat? = null
    private var controller: MediaControllerCompat? = null

    fun connect() = mainThread {
        disconnectImmediate()
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, MusicPlayerService::class.java),
            ConnectionCallback(),
            null
        ).apply { connect() }
    }

    fun disconnect() = mainThread {
        disconnectImmediate()
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

    private fun detachMediaBrowser() {
        mediaBrowser?.disconnect()
        mediaBrowser = null
    }

    private fun detachController() {
        controller?.unregisterCallback(controllerCallback)
        controller = null
    }

    private fun updateMetadata(metadata: MediaMetadataCompat?) {
        mediaFailureHandler.onMetadataChanged(metadata)
    }

    private fun updatePlaybackState(state: PlaybackStateCompat?) {
        mediaFailureHandler.onPlaybackStateChanged(state)
    }

    private inline fun mainThread(crossinline block: () -> Unit) {
        mainThreadHandler.post { block() }
    }

    private inner class ConnectionCallback : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaBrowser?.let { mediaBrowser ->
                attachController(MediaControllerCompat(context, mediaBrowser.sessionToken))
            }
        }

        override fun onConnectionFailed() {
            analytics?.log(MediaPlayerError("Assistant session media browser connection failed"))
            logger?.e(
                Tag.ASSISTANT_MEDIA,
                MediaPlayerConnectException("Assistant session media browser connection failed")
            )
        }
    }

    private inner class ControllerCallback : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            updateMetadata(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updatePlaybackState(state)
        }
    }
}