package ru.mail.search.assistant.services.notification

import android.app.Notification

interface PlayerNotificationManager {

    /**
     * The identifier for player notification as per
     * {@link NotificationManager#notify(int, Notification)
     * Used to start foreground service.
     */
    val notificationId: Int

    fun showNotification(properties: PlayerNotificationProperties): Notification
}