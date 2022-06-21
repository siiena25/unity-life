package ru.mail.search.assistant.services.music.action

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.R
import ru.mail.search.assistant.services.music.extension.CUSTOM_ACTION_FAST_BACKWARD
import ru.mail.search.assistant.services.music.extension.getTime

internal class FastBackwardActionProvider : MediaSessionConnector.CustomActionProvider {

    override fun getCustomAction(player: Player): PlaybackStateCompat.CustomAction {
        return PlaybackStateCompat.CustomAction.Builder(
            CUSTOM_ACTION_FAST_BACKWARD,
            "Fast backward",
            R.drawable.exo_icon_play
        ).build()
    }

    override fun onCustomAction(
        player: Player,
        controlDispatcher: ControlDispatcher,
        action: String,
        extras: Bundle?
    ) {
        extras?.getTime()
            ?.let { time -> player.seekTo(player.currentPosition - time) }
    }
}