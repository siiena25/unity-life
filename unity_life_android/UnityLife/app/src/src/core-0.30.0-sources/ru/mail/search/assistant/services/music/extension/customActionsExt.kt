package ru.mail.search.assistant.services.music.extension

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import androidx.annotation.IntRange

const val CUSTOM_ACTION_FAST_FORWARD =
    "ru.mail.search.assistant.media.action.FAST_FORWARD"
const val CUSTOM_ACTION_FAST_BACKWARD =
    "ru.mail.search.assistant.media.action.FAST_BACKWARD"
private const val ARGUMENT_TIME = "time"

const val CUSTOM_ACTION_SET_USER_VOLUME =
    "ru.mail.search.assistant.media.action.SET_USER_VOLUME"
private const val ARGUMENT_VOLUME = "volume"

const val CUSTOM_ACTION_DUCK_VOLUME =
    "ru.mail.search.assistant.media.action.DUCK_VOLUME"
const val CUSTOM_ACTION_UNDUCK_VOLUME =
    "ru.mail.search.assistant.media.action.UNDUCK_VOLUME"
const val CUSTOM_ACTION_SKIP_TO_PREVIOUS_DEFINITELY =
    "ru.mail.search.assistant.media.action.SKIP_TO_PREVIOUS_DEFINITELY"
const val CUSTOM_ACTION_REWIND =
    "ru.mail.search.assistant.media.action.REWIND"

const val MEDIA_BUNDLE_TRACK_NUMBER = "ru.mail.search.assistant.media.play.TRACK_NUMBER"
const val MEDIA_BUNDLE_TRACK_POSITION = "ru.mail.search.assistant.media.play.TRACK_POSITION"

fun MediaControllerCompat.TransportControls.fastForward(time: Long) {
    sendCustomAction(
        CUSTOM_ACTION_FAST_FORWARD,
        Bundle().apply { putLong(ARGUMENT_TIME, time) }
    )
}

fun MediaControllerCompat.TransportControls.fastBackward(time: Long) {
    sendCustomAction(
        CUSTOM_ACTION_FAST_BACKWARD,
        Bundle().apply { putLong(ARGUMENT_TIME, time) }
    )
}

fun MediaControllerCompat.TransportControls.setUserVolume(
    @IntRange(from = 0, to = 100) volume: Int
) {
    sendCustomAction(
        CUSTOM_ACTION_SET_USER_VOLUME,
        Bundle().apply { putInt(ARGUMENT_VOLUME, volume) }
    )
}

fun MediaControllerCompat.TransportControls.duckVolume() {
    sendCustomAction(CUSTOM_ACTION_DUCK_VOLUME, null)
}

fun MediaControllerCompat.TransportControls.unduckVolume() {
    sendCustomAction(CUSTOM_ACTION_UNDUCK_VOLUME, null)
}

fun MediaControllerCompat.TransportControls.skipToPreviousDefinitely() {
    sendCustomAction(CUSTOM_ACTION_SKIP_TO_PREVIOUS_DEFINITELY, null)
}

fun MediaControllerCompat.TransportControls.rewind(trackNumber: Int, trackPosition: Long) {
    sendCustomAction(CUSTOM_ACTION_REWIND, Bundle().apply {
        putInt(MEDIA_BUNDLE_TRACK_NUMBER, trackNumber)
        putLong(MEDIA_BUNDLE_TRACK_POSITION, trackPosition)
    })
}

fun Bundle.getTime(): Long {
    return getLong(ARGUMENT_TIME)
}

@IntRange(from = 0, to = 100)
fun Bundle.getVolume(): Int {
    return getInt(ARGUMENT_VOLUME)
}

fun MediaControllerCompat.TransportControls.playFromMediaId(
    mediaId: String,
    trackNumber: Int,
    trackPosition: Long
) {
    val extras = Bundle().apply {
        putInt(MEDIA_BUNDLE_TRACK_NUMBER, trackNumber)
        putLong(MEDIA_BUNDLE_TRACK_POSITION, trackPosition)
    }
    playFromMediaId(mediaId, extras)
}

fun MediaControllerCompat.TransportControls.prepareFromMediaId(
    mediaId: String,
    trackNumber: Int,
    trackPosition: Long
) {
    val extras = Bundle().apply {
        putInt(MEDIA_BUNDLE_TRACK_NUMBER, trackNumber)
        putLong(MEDIA_BUNDLE_TRACK_POSITION, trackPosition)
    }
    prepareFromMediaId(mediaId, extras)
}