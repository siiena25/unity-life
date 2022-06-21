package ru.mail.search.assistant.services.music

import android.graphics.Bitmap
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat.QueueItem
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector.MediaMetadataProvider
import ru.mail.search.assistant.services.music.callback.cover.CoverManager
import ru.mail.search.assistant.services.music.extension.setAlbumArt
import ru.mail.search.assistant.services.music.extension.setAlbumArtLoadingStage
import ru.mail.search.assistant.services.music.extension.setUserVolume

/**
 * Copy of [MediaSessionConnector.DefaultMediaMetadataProvider] v2.10.3 with album art loading.
 */
internal class CustomMediaMetadataProvider(
    private val mediaController: MediaControllerCompat,
    private val coverManager: CoverManager,
    private val volumeManager: VolumeManager
) : MediaMetadataProvider {

    private val emptyMetadata: MediaMetadataCompat = MediaMetadataCompat.Builder().build()

    override fun getMetadata(player: Player): MediaMetadataCompat {
        if (player.currentTimeline.isEmpty) {
            return emptyMetadata
        }
        val builder = MediaMetadataCompat.Builder()
        if (player.isPlayingAd) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_ADVERTISEMENT, 1)
        }
        builder.putLong(
            MediaMetadataCompat.METADATA_KEY_DURATION,
            if (player.duration == C.TIME_UNSET) -1 else player.duration
        )
        val activeQueueItemId: Long = mediaController.playbackState.activeQueueItemId
        if (activeQueueItemId != QueueItem.UNKNOWN_ID.toLong()) {
            val queue: List<QueueItem>? = mediaController.queue
            var i = 0
            while (queue != null && i < queue.size) {
                val queueItem = queue[i]
                if (queueItem.queueId == activeQueueItemId) {
                    val description: MediaDescriptionCompat = queueItem.description
                    val extras = description.extras
                    if (extras != null) {
                        for (key in extras.keySet()) {
                            when (val value: Any? = extras.get(key)) {
                                is String -> builder.putString(key, value)
                                is CharSequence -> builder.putText(key, value)
                                is Long -> builder.putLong(key, value)
                                is Int -> builder.putLong(key, value.toLong())
                                is Bitmap -> builder.putBitmap(key, value)
                                is RatingCompat -> builder.putRating(key, value)
                            }
                        }
                    }
                    if (description.title != null) {
                        val title = description.title.toString()
                        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                    }
                    if (description.subtitle != null) {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                            description.subtitle.toString()
                        )
                    }
                    if (description.description != null) {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                            description.description.toString()
                        )
                    }
                    if (description.iconBitmap != null) {
                        builder.putBitmap(
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
                            description.iconBitmap
                        )
                    }
                    if (description.iconUri != null) {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                            description.iconUri.toString()
                        )
                    }
                    if (description.mediaId != null) {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                            description.mediaId.toString()
                        )
                    }
                    if (description.mediaUri != null) {
                        builder.putString(
                            MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                            description.mediaUri.toString()
                        )
                    }
                    coverManager.requestCover(description.iconUri)?.let { coverState ->
                        builder.setAlbumArt(coverState.bitmap)
                        builder.setAlbumArtLoadingStage(coverState.stage)
                    }
                    val volume = volumeManager.getUserVolume()
                    if (volume > 0) {
                        builder.setUserVolume(volume)
                    } else {
                        builder.setUserVolume(VolumeManager.getUserVolume(player))
                    }
                    break
                }
                i++
            }
        }
        return builder.build()
    }
}