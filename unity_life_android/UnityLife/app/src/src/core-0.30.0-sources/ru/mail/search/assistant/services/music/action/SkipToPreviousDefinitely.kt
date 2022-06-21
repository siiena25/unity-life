package ru.mail.search.assistant.services.music.action

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline.Window
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.R
import ru.mail.search.assistant.services.music.extension.CUSTOM_ACTION_SKIP_TO_PREVIOUS_DEFINITELY

internal class SkipToPreviousDefinitely : MediaSessionConnector.CustomActionProvider {

    private companion object {
        private const val MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000L
    }

    private val window = Window()

    override fun getCustomAction(player: Player): PlaybackStateCompat.CustomAction {
        return PlaybackStateCompat.CustomAction.Builder(
            CUSTOM_ACTION_SKIP_TO_PREVIOUS_DEFINITELY,
            "Skip to previous",
            R.drawable.exo_icon_previous
        ).build()
    }

    override fun onCustomAction(
        player: Player,
        controlDispatcher: ControlDispatcher,
        action: String,
        extras: Bundle?
    ) {
        val previousWindowIndex = player.previousWindowIndex
        if (previousWindowIndex != C.INDEX_UNSET) {
            controlDispatcher.dispatchSeekTo(player, previousWindowIndex, C.TIME_UNSET)
            return
        }

        val windowIndex = player.currentWindowIndex
        val timeline = player.currentTimeline
        if (timeline.isEmpty || player.isPlayingAd) {
            return
        }

        timeline.getWindow(windowIndex, window)
        if (player.currentPosition > MAX_POSITION_FOR_SEEK_TO_PREVIOUS &&
            window.isSeekable
        ) {
            controlDispatcher.dispatchSeekTo(player, windowIndex, 0)
        }
    }
}