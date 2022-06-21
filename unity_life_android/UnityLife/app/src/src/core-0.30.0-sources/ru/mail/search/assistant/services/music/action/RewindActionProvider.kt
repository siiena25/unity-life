package ru.mail.search.assistant.services.music.action

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.R
import ru.mail.search.assistant.services.music.extension.CUSTOM_ACTION_REWIND
import ru.mail.search.assistant.services.music.extension.MEDIA_BUNDLE_TRACK_NUMBER
import ru.mail.search.assistant.services.music.extension.MEDIA_BUNDLE_TRACK_POSITION

internal class RewindActionProvider : MediaSessionConnector.CustomActionProvider {

    override fun getCustomAction(player: Player): PlaybackStateCompat.CustomAction =
        PlaybackStateCompat.CustomAction.Builder(
            CUSTOM_ACTION_REWIND,
            "Rewind",
            R.drawable.exo_icon_play
        ).build()

    override fun onCustomAction(
        player: Player,
        controlDispatcher: ControlDispatcher,
        action: String,
        extras: Bundle?
    ) {
        extras ?: return
        val position = extras.getInt(MEDIA_BUNDLE_TRACK_NUMBER)
        val elapsed = extras.getLong(MEDIA_BUNDLE_TRACK_POSITION)
        if (position in 0 until player.currentTimeline.windowCount) {
            controlDispatcher.dispatchSeekTo(player, position, elapsed)
        }
    }
}