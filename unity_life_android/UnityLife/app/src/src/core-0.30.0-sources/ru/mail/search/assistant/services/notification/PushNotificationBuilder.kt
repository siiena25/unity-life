package ru.mail.search.assistant.services.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class PushNotificationBuilder(
    private val context: Context,
    @DrawableRes private val notificationIcon: Int,
    @ColorRes private val notificationColor: Int
) {

    fun buildNotification(
        title: String,
        text: String,
        pendingIntent: PendingIntent,
        channelId: String
    ): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
        return builder
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, notificationColor))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(text)
            )
            .setDefaults(Notification.DEFAULT_ALL)
            .build()
    }
}