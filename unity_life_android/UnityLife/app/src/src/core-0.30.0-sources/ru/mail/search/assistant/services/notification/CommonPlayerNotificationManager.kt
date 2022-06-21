package ru.mail.search.assistant.services.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import ru.mail.search.assistant.common.util.Logger

class CommonPlayerNotificationManager(
    context: Context,
    override val notificationId: Int,
    private val channelId: String,
    resourcesProvider: PlayerNotificationResourcesProvider,
    logger: Logger?,
) : PlayerNotificationManager {

    private val notificationBuilder = PlayerNotificationBuilder(context, resourcesProvider, logger)
    private val notificationManager = NotificationManagerCompat.from(context)

    override fun showNotification(properties: PlayerNotificationProperties): Notification {
        val notification = notificationBuilder.buildNotification(channelId, properties)
        notificationManager.notify(notificationId, notification)
        return notification
    }
}