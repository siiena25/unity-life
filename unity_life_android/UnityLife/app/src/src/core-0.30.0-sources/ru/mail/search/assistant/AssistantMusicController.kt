package ru.mail.search.assistant

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatTrigger
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.PlayerEventRepository
import ru.mail.search.assistant.data.exception.MediaPlayerConnectException
import ru.mail.search.assistant.services.music.MusicPlayerService
import ru.mail.search.assistant.services.music.extension.rewind
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError

class AssistantMusicController(
    private val context: Context,
    private val analytics: Analytics?,
    private val playerEventRepository: PlayerEventRepository,
    private val logger: Logger?
) {

    companion object {
        private const val TAG = "AssistantMusicControl"
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var mediaBrowser: MediaBrowserCompat? = null
    private var controller: MediaControllerCompat? = null
    private val transportControls get() = controller?.transportControls

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

    fun stop() = mainThread {
        playerEventRepository.onTrackStop(
            PlayerDeviceStatTrigger.APP_UI,
            controller
        )
        transportControls?.rewind(0, 0)
        transportControls?.stop()
    }

    private fun attachController(controller: MediaControllerCompat) {
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
        controller = null
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
            analytics?.log(MediaPlayerError("Assistant media browser connection failed"))
            logger?.e(TAG, MediaPlayerConnectException("Assistant media browser connection failed"))
        }
    }
}