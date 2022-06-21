package ru.mail.search.assistant.services.music

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator

internal class PlayerQueueNavigator(
    mediaSession: MediaSessionCompat
) : TimelineQueueNavigator(mediaSession) {

    private val window = Timeline.Window()

    override fun getSupportedQueueNavigatorActions(player: Player): Long {
        var enableSkipTo = false
        var enablePrevious = false
        var enableNext = false
        val timeline = player.currentTimeline
        if (!timeline.isEmpty && !player.isPlayingAd) {
            timeline.getWindow(player.currentWindowIndex, window)
            enableSkipTo = timeline.windowCount > 1
            enablePrevious = window.isSeekable || player.hasPreviousWindow()
            enableNext = player.hasNextWindow()
        }

        var actions: Long = 0
        if (enableSkipTo) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
        }
        if (enablePrevious) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        }
        if (enableNext) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }
        return actions
    }

    override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
        return player.currentTimeline
            .getWindow(windowIndex, window)
            .mediaItem.playbackProperties?.tag as MediaDescriptionCompat
    }
}