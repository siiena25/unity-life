package ru.mail.search.assistant.media

import android.os.Build
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * Visualizer disabled for Android 5.0 version because of native crash.
 * See EL-1725.
 */
internal class TTSPlayer(
    private val exoPlayer: SimpleExoPlayer,
    private val audioLevelInterceptor: AudioLevelInterceptor
) : ExoPlayer by exoPlayer {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            audioLevelInterceptor.init(exoPlayer.audioSessionId)
        }
    }

    override fun release() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            audioLevelInterceptor.onRelease()
        }
        exoPlayer.release()
    }
}