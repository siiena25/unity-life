package ru.mail.search.assistant.services.notification

import android.app.PendingIntent
import android.graphics.Bitmap
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class PlayerNotificationProperties(
    val contentIntent: PendingIntent,
    val sessionToken: MediaSessionCompat.Token,
    val playbackState: PlaybackStateCompat,
    val mediaDescription: MediaDescriptionCompat,
    val isPlaying: Boolean,
    val largeIcon: Bitmap?
)