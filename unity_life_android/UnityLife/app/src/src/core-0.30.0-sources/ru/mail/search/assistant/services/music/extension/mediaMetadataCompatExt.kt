package ru.mail.search.assistant.services.music.extension

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.IntRange
import ru.mail.search.assistant.entities.audio.AudioSource
import ru.mail.search.assistant.services.music.callback.cover.CoverManager

const val METADATA_KEY_MESSAGE_ID = "services.music.media_metadata.MESSAGE_ID"
const val METADATA_KEY_DURATION = "services.music.media_metadata.DURATION"
const val METADATA_KEY_IS_VK = "services.music.media_metadata.IS_VK"
const val METADATA_KEY_LIMIT = "services.music.media_metadata.LIMIT"
const val METADATA_KEY_ALBUM_ART_LOADING_STAGE =
    "services.music.media_metadata.ALBUM_ART_LOADING_STAGE"
const val METADATA_KEY_TRACK_ID = "services.music.media_metadata.TRACK_ID"
const val METADATA_KEY_TRACK_INDEX = "services.music.media_metadata.TRACK_INDEX"
const val METADATA_KEY_TRACK_SOURCE_TYPE = "services.music.media_metadata.TRACK_SOURCE_TYPE"
const val METADATA_KEY_TRACK_PHRASE_ID = "services.music.media_metadata.TRACK_PHRASE_ID"
const val METADATA_KEY_TRACK_STAT_FLAGS = "services.music.media_metadata.TRACK_STAT_FLAGS"
const val METADATA_KEY_TRACK_SOURCE = "services.music.media_metadata.SOURCE"
const val METADATA_KEY_IS_MUSIC = "services.music.media_metadata.IS_MUSIC"
const val METADATA_KEY_USER_VOLUME = "services.music.media_metadata.USER_VOLUME"

const val METADATA_KEY_AUDIO_SOURCE_MEDIA_TYPE =
    "services.music.media_metadata.audio_source.MEDIA_TYPE"
const val METADATA_KEY_AUDIO_SOURCE_UID =
    "services.music.media_metadata.audio_source.UID"
const val METADATA_KEY_AUDIO_SOURCE_SKILL_NAME =
    "services.music.media_metadata.audio_source.SKILL_NAME"

/**
 * Useful extensions for [MediaMetadataCompat].
 */
inline val MediaMetadataCompat.mediaId: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.messageId: Long?
    get() = bundle.getLong(METADATA_KEY_MESSAGE_ID, -1L).takeIf { it >= 0 }

inline val MediaMetadataCompat.isVk: Boolean
    get() = getLong(METADATA_KEY_IS_VK) > 0

inline val MediaMetadataCompat.limit: Long
    get() = getLong(METADATA_KEY_LIMIT)

inline val MediaMetadataCompat.title: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.artist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.duration
    get() = getLong(METADATA_KEY_DURATION)
        .takeIf { duration -> duration > 0 }
        ?: getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            .takeIf { duration -> duration > 0 }

inline val MediaMetadataCompat.date: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DATE)

inline val MediaMetadataCompat.albumArt: Bitmap?
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)

inline val MediaMetadataCompat.rating
    get() = getLong(MediaMetadataCompat.METADATA_KEY_RATING)

inline val MediaMetadataCompat.displayTitle: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadataCompat.displaySubtitle: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)

inline val MediaMetadataCompat.displayIconUri: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)

inline val MediaMetadataCompat.mediaUri: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)

inline val MediaMetadataCompat.albumArtLoadingStage
    get() = CoverManager.Stage.values()[getLong(METADATA_KEY_ALBUM_ART_LOADING_STAGE).toInt()]

inline val MediaMetadataCompat.trackIndex
    get() = getLong(METADATA_KEY_TRACK_INDEX).toInt()

inline val MediaMetadataCompat.sourceType: String?
    get() = getString(METADATA_KEY_TRACK_SOURCE_TYPE)

inline val MediaMetadataCompat.trackPhraseId: String?
    get() = getString(METADATA_KEY_TRACK_PHRASE_ID)

inline val MediaMetadataCompat.statFlags: String?
    get() = getString(METADATA_KEY_TRACK_STAT_FLAGS)

inline val MediaMetadataCompat.audioSource: AudioSource?
    get() {
        return AudioSource(
            mediaType = getString(METADATA_KEY_AUDIO_SOURCE_MEDIA_TYPE),
            uid = getString(METADATA_KEY_AUDIO_SOURCE_UID),
            sourceType = getString(METADATA_KEY_TRACK_SOURCE_TYPE),
            skillName = getString(METADATA_KEY_AUDIO_SOURCE_SKILL_NAME),
            source = getString(METADATA_KEY_TRACK_SOURCE)
        )
    }

inline val MediaMetadataCompat.userVolume: Int
    @IntRange(from = 0, to = 100)
    get() = getLong(METADATA_KEY_USER_VOLUME).toInt()

/**
 * Custom property for retrieving a [MediaDescriptionCompat] which also includes
 * all of the keys from the [MediaMetadataCompat] object in its extras.
 *
 * These keys are used by the ExoPlayer MediaSession extension when announcing metadata changes.
 */
inline val MediaMetadataCompat.customDescription: MediaDescriptionCompat
    get() = description.let { description ->
        val extras = description.extras
        if (extras == null) {
            MediaDescriptionCompat.Builder()
                .setMediaId(description.mediaId)
                .setTitle(description.title)
                .setSubtitle(description.subtitle)
                .setDescription(description.description)
                .setIconBitmap(description.iconBitmap)
                .setIconUri(description.iconUri)
                .setExtras(Bundle().also { bundle -> bundle.fillCustomDescription(this) })
                .setMediaUri(description.mediaUri)
                .build()
        } else {
            extras.fillCustomDescription(this)
            description
        }
    }

fun Bundle.fillCustomDescription(description: MediaMetadataCompat) {
    description.messageId?.let { messageId -> putLong(METADATA_KEY_MESSAGE_ID, messageId) }
    copyLongFrom(description, METADATA_KEY_IS_VK)
    copyLongFrom(description, METADATA_KEY_LIMIT)
    copyLongFrom(description, METADATA_KEY_DURATION)
    copyLongFrom(description, METADATA_KEY_ALBUM_ART_LOADING_STAGE)
    copyLongFrom(description, METADATA_KEY_TRACK_ID)
    copyLongFrom(description, METADATA_KEY_TRACK_INDEX)
    copyLongFrom(description, METADATA_KEY_IS_MUSIC)
    copyLongFrom(description, METADATA_KEY_USER_VOLUME)
    copyStringFrom(description, METADATA_KEY_TRACK_SOURCE_TYPE)
    copyStringFrom(description, METADATA_KEY_TRACK_PHRASE_ID)
    copyStringFrom(description, METADATA_KEY_TRACK_STAT_FLAGS)
    copyStringFrom(description, METADATA_KEY_AUDIO_SOURCE_MEDIA_TYPE)
    copyStringFrom(description, METADATA_KEY_AUDIO_SOURCE_UID)
    copyStringFrom(description, METADATA_KEY_AUDIO_SOURCE_SKILL_NAME)
    copyStringFrom(description, METADATA_KEY_TRACK_SOURCE)

    copyStringFrom(description, MediaMetadataCompat.METADATA_KEY_ARTIST)
    copyStringFrom(description, MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)
    copyLongFrom(description, MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)
    copyLongFrom(description, MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)
}

private fun Bundle.copyLongFrom(description: MediaMetadataCompat, key: String) {
    putLong(key, description.getLong(key))
}

private fun Bundle.copyStringFrom(description: MediaMetadataCompat, key: String) {
    putString(key, description.getString(key))
}

fun MediaMetadataCompat.Builder.setMediaId(mediaId: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
}

fun MediaMetadataCompat.Builder.setMessageId(messageId: Long): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_MESSAGE_ID, messageId)
}

fun MediaMetadataCompat.Builder.setDisplayTitle(title: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
}

fun MediaMetadataCompat.Builder.setDisplaySubtitle(subtitle: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
}

fun MediaMetadataCompat.Builder.setArtist(artist: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
}

fun MediaMetadataCompat.Builder.setAlbumArtist(artist: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, artist)
}

fun MediaMetadataCompat.Builder.setAlbumArt(bitmap: Bitmap?): MediaMetadataCompat.Builder {
    return putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
}

fun MediaMetadataCompat.Builder.setAlbumArtLoadingStage(stage: CoverManager.Stage): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_ALBUM_ART_LOADING_STAGE, stage.ordinal.toLong())
}

fun MediaMetadataCompat.Builder.setDisplayIconUri(uri: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, uri)
}

fun MediaMetadataCompat.Builder.setDuration(duration: Long): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_DURATION, duration)
}

fun MediaMetadataCompat.Builder.setTrackCount(count: Int): MediaMetadataCompat.Builder {
    return putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, count.toLong())
}

fun MediaMetadataCompat.Builder.setTrackIndex(index: Int): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_TRACK_INDEX, index.toLong())
}

fun MediaMetadataCompat.Builder.setTrackNumber(number: Int): MediaMetadataCompat.Builder {
    return putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, number.toLong())
}

fun MediaMetadataCompat.Builder.setMediaUri(uri: String): MediaMetadataCompat.Builder {
    return putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, uri)
}

fun MediaMetadataCompat.Builder.setIsVk(isVk: Boolean): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_IS_VK, if (isVk) 1 else 0)
}

fun MediaMetadataCompat.Builder.setLimit(limit: Long): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_LIMIT, limit)
}

fun MediaMetadataCompat.Builder.setTrackId(trackId: Long): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_TRACK_ID, trackId)
}

fun MediaMetadataCompat.Builder.setPhraseId(phraseId: String): MediaMetadataCompat.Builder {
    return putString(METADATA_KEY_TRACK_PHRASE_ID, phraseId)
}

fun MediaMetadataCompat.Builder.setStatFlags(statFlags: String): MediaMetadataCompat.Builder {
    return putString(METADATA_KEY_TRACK_STAT_FLAGS, statFlags)
}

fun MediaMetadataCompat.Builder.setSource(source: String?): MediaMetadataCompat.Builder {
    return putString(METADATA_KEY_TRACK_SOURCE, source)
}

fun MediaMetadataCompat.Builder.setIsMusic(isMusic: Boolean): MediaMetadataCompat.Builder {
    return putLong(METADATA_KEY_IS_MUSIC, if (isMusic) 1 else 0)
}

fun MediaMetadataCompat.Builder.setAudioSource(audioSource: AudioSource): MediaMetadataCompat.Builder {
    putString(METADATA_KEY_AUDIO_SOURCE_MEDIA_TYPE, audioSource.mediaType)
    putString(METADATA_KEY_AUDIO_SOURCE_UID, audioSource.uid)
    putString(METADATA_KEY_TRACK_SOURCE_TYPE, audioSource.sourceType)
    putString(METADATA_KEY_AUDIO_SOURCE_SKILL_NAME, audioSource.skillName)
    putString(METADATA_KEY_TRACK_SOURCE, audioSource.source)
    return this
}

fun MediaMetadataCompat.Builder.setUserVolume(
    @IntRange(from = 0, to = 100) volume: Int
): MediaMetadataCompat.Builder {
    putLong(METADATA_KEY_USER_VOLUME, volume.toLong())
    return this
}