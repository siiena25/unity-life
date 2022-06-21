package ru.mail.search.assistant.services.music.model

import android.support.v4.media.MediaMetadataCompat

data class PlaybackState(
    val position: Int,
    val playlist: List<MediaMetadataCompat>
)