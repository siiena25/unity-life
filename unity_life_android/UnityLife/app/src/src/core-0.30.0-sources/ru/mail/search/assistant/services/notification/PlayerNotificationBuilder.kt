package ru.mail.search.assistant.services.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.services.music.extension.isSkipToNextEnabled
import ru.mail.search.assistant.services.music.extension.isSkipToPreviousEnabled

class PlayerNotificationBuilder(
    private val context: Context,
    private val resourcesProvider: PlayerNotificationResourcesProvider,
    private val logger: Logger?,
) {

    private companion object {

        const val TAG = "PlayerNotifications"
    }

    private val skipToPreviousAction = NotificationCompat.Action(
        resourcesProvider.controlPreviousIconRes,
        context.getString(resourcesProvider.controlPreviousLabelRes),
        createPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS),
    )
    private val playAction = NotificationCompat.Action(
        resourcesProvider.controlPlayIconRes,
        context.getString(resourcesProvider.controlPlayLabelRes),
        createPendingIntent(PlaybackStateCompat.ACTION_PLAY),
    )
    private val pauseAction = NotificationCompat.Action(
        resourcesProvider.controlPauseIconRes,
        context.getString(resourcesProvider.controlPauseLabelRes),
        createPendingIntent(PlaybackStateCompat.ACTION_PAUSE),
    )
    private val skipToNextAction = NotificationCompat.Action(
        resourcesProvider.controlNextIconRes,
        context.getString(resourcesProvider.controlNextLabelRes),
        createPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT),
    )
    private val stopPendingIntent = createPendingIntent(PlaybackStateCompat.ACTION_STOP)

    fun buildNotification(
        channelId: String,
        properties: PlayerNotificationProperties
    ): Notification {
        val builder = NotificationCompat.Builder(context, channelId)

        val playbackState = properties.playbackState
        val isNavigationControlsEnabled =
            playbackState.isSkipToPreviousEnabled || playbackState.isSkipToNextEnabled
        if (isNavigationControlsEnabled) {
            builder.addAction(skipToPreviousAction)
        }
        if (properties.isPlaying) {
            builder.addAction(pauseAction)
        } else {
            builder.addAction(playAction)
        }
        if (isNavigationControlsEnabled) {
            builder.addAction(skipToNextAction)
        }

        val miniPlayerActions = if (isNavigationControlsEnabled) {
            intArrayOf(0, 1, 2)
        } else {
            intArrayOf(0)
        }

        val description = properties.mediaDescription
        return builder.setContentIntent(properties.contentIntent)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .apply {
                stopPendingIntent?.let(::setDeleteIntent)
            }
            .setSmallIcon(resourcesProvider.smallIconRes)
            .setLargeIcon(properties.largeIcon)
            .setOngoing(properties.isPlaying)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setupStyle(properties.sessionToken, miniPlayerActions)
            .build()
    }

    private fun createPendingIntent(action: Long): PendingIntent? {
        return runCatching {
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, action)
        }.getOrElse { error ->
            logger?.e(TAG, error, "Failed to create pending intent with action=$action")
            null
        }
    }

    @SuppressWarnings("SpreadOperator")
    private fun NotificationCompat.Builder.setupStyle(
        sessionToken: MediaSessionCompat.Token,
        compactViewAction: IntArray
    ): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            || !Build.MANUFACTURER.contains("huawei", ignoreCase = true)
        ) {
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .run {
                        stopPendingIntent?.let(::setCancelButtonIntent) ?: this
                    }
                    .setMediaSession(sessionToken)
                    .setShowActionsInCompactView(*compactViewAction)
                    .setShowCancelButton(true)
            )
        }
        return this
    }
}