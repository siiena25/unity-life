package ru.mail.search.assistant.services.music.callback.noisy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.media.session.MediaControllerCompat
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatTrigger
import ru.mail.search.assistant.data.PlayerEventRepository

/**
 * Helper class for listening for when headphones are unplugged (or the audio
 * will otherwise cause playback to become "noisy").
 */
internal class BecomingNoisyReceiver(
    private val context: Context,
    private val controller: MediaControllerCompat,
    private val playerEventRepository: PlayerEventRepository
) : BroadcastReceiver() {

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var registered = false

    fun register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            controller.transportControls.pause()
            playerEventRepository.onTrackPause(
                PlayerDeviceStatTrigger.APP_UI,
                controller
            )
        }
    }
}