package ru.mail.search.assistant.services.music.action

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.R
import ru.mail.search.assistant.services.music.VolumeManager
import ru.mail.search.assistant.services.music.extension.CUSTOM_ACTION_DUCK_VOLUME

internal class DuckVolumeActionProvider(
    private val volumeManager: VolumeManager
) : MediaSessionConnector.CustomActionProvider {

    override fun getCustomAction(player: Player): PlaybackStateCompat.CustomAction {
        return PlaybackStateCompat.CustomAction.Builder(
            CUSTOM_ACTION_DUCK_VOLUME,
            "Duck volume",
            R.drawable.exo_icon_play
        ).build()
    }

    override fun onCustomAction(
        player: Player,
        controlDispatcher: ControlDispatcher,
        action: String,
        extras: Bundle?
    ) {
        volumeManager.duckVolume(player)
    }
}