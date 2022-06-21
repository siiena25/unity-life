package ru.mail.search.assistant.services.music.callback.notification

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.content.ContextCompat
import ru.mail.search.assistant.services.music.MusicPlayerService
import ru.mail.search.assistant.services.music.callback.BaseMediaControllerCallback
import ru.mail.search.assistant.services.music.callback.cover.CoverManager
import ru.mail.search.assistant.services.music.extension.albumArt
import ru.mail.search.assistant.services.music.extension.albumArtLoadingStage
import ru.mail.search.assistant.services.notification.PlayerNotificationManager
import ru.mail.search.assistant.services.notification.PlayerNotificationProperties

internal class PlayerNotificationCallback(
    private val service: MusicPlayerService,
    private val mediaController: MediaControllerCompat,
    private val notificationManager: PlayerNotificationManager?
) : BaseMediaControllerCallback() {

    private var isActive = false
    private var isPlaying = false

    override fun onPrepare() {
        isActive = true
    }

    override fun onPlay() {
        isActive = true
        isPlaying = true
        updateNotificationImmediate()?.let(::startForeground)
    }

    override fun onPause() {
        isPlaying = false
        updateNotificationImmediate()
        service.stopForeground(false)
    }

    override fun onStop() {
        isPlaying = false
        isActive = false
        service.stopForeground(true)
        service.stopSelf()
    }

    override fun onMetadataChanged() {
        if (isActive) {
            updateNotificationLazy()
        }
    }

    private fun startForeground(notification: Notification) {
        notificationManager ?: return
        val intent = Intent(service.applicationContext, MusicPlayerService::class.java)
        ContextCompat.startForegroundService(service.applicationContext, intent)
        service.startForeground(notificationManager.notificationId, notification)
    }

    private fun updateNotificationImmediate(): Notification? {
        notificationManager ?: return null
        val metadata = mediaController.metadata
        val description = metadata.description
        val largeIcon = metadata.albumArt
        return showNotification(notificationManager, description, largeIcon)
    }

    private fun updateNotificationLazy() {
        notificationManager ?: return
        val metadata = mediaController.metadata
        val description = metadata.description
        val iconUri = description.iconUri
        val iconLarge = metadata.albumArt
        if (iconUri == null || metadata.albumArtLoadingStage != CoverManager.Stage.CACHE) {
            showNotification(notificationManager, description, iconLarge)
        }
    }

    private fun showNotification(
        notificationManager: PlayerNotificationManager,
        description: MediaDescriptionCompat,
        largeIcon: Bitmap?
    ): Notification {
        val properties = PlayerNotificationProperties(
            contentIntent = mediaController.sessionActivity,
            sessionToken = mediaController.sessionToken,
            playbackState = mediaController.playbackState,
            mediaDescription = description,
            isPlaying = isPlaying,
            largeIcon = largeIcon
        )
        return notificationManager.showNotification(properties)
    }
}