package ru.mail.search.assistant.services.music.callback

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError
import ru.mail.search.assistant.util.analytics.event.MediaStartPlaying

internal class MediaControllerCallback(
    private val customCallbacks: List<CustomCallback>,
    private val analytics: Analytics?
) : MediaControllerCompat.Callback() {

    private var playerState = PlayerState.IDLE
    private var wasPlaying = false

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        customCallbacks.forEach { it.onMetadataChanged() }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        when {
            state.isPlaying || state.isContinuePlaying -> {
                if (playerState < PlayerState.PLAYING) {
                    onPlay()
                }
            }
            state.isPaused -> {
                if (playerState > PlayerState.PREPARED) {
                    onPause()
                } else if (playerState < PlayerState.PREPARED) {
                    onPrepare()
                }
            }
            state.isStopped -> {
                if (playerState != PlayerState.STOPPED) {
                    onStop()
                }
            }
        }
        if (state.isError) {
            val message: CharSequence? = state.errorMessage
            onError(state.errorCode, message.toString())
        }
    }

    private fun onPrepare() {
        wasPlaying = false
        playerState = PlayerState.PREPARED
        customCallbacks.forEach { it.onPrepare() }
    }

    private fun onPlay() {
        wasPlaying = true
        playerState = PlayerState.PLAYING
        customCallbacks.forEach { it.onPlay() }
        analytics?.log(MediaStartPlaying())
    }

    private fun onPause() {
        wasPlaying = false
        playerState = PlayerState.PREPARED
        customCallbacks.forEach { it.onPause() }
    }

    private fun onStop() {
        wasPlaying = false
        playerState = PlayerState.STOPPED
        customCallbacks.forEach { it.onStop() }
    }

    private fun onError(errorCode: Int, errorMessage: String) {
        analytics?.log(MediaPlayerError("Music player error ($errorCode): $errorMessage"))
    }

    /**
     * Keep correct order of states
     *
     * IDLE -> STOPPED -> PREPARED -> PLAYING
     */
    enum class PlayerState {

        IDLE, STOPPED, PREPARED, PLAYING
    }

    private inline val PlaybackStateCompat.isPlaying
        get() = (state == PlaybackStateCompat.STATE_PLAYING)

    private inline val PlaybackStateCompat.isContinuePlaying
        get() = (state == PlaybackStateCompat.STATE_BUFFERING && wasPlaying)

    private inline val PlaybackStateCompat.isPaused
        get() = (state == PlaybackStateCompat.STATE_PAUSED) ||
                (state == PlaybackStateCompat.STATE_ERROR)

    private inline val PlaybackStateCompat.isStopped
        get() = (state == PlaybackStateCompat.STATE_STOPPED) ||
                (state == PlaybackStateCompat.STATE_NONE)

    private inline val PlaybackStateCompat.isError
        get() = (state == PlaybackStateCompat.STATE_ERROR)

    interface CustomCallback {

        fun onPrepare()

        fun onPlay()

        fun onPause()

        fun onStop()

        fun onMetadataChanged()
    }
}