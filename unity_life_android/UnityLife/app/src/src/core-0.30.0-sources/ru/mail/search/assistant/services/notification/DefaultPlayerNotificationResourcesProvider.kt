package ru.mail.search.assistant.services.notification

import ru.mail.search.assistant.R

class DefaultPlayerNotificationResourcesProvider : PlayerNotificationResourcesProvider {

    override val smallIconRes: Int
        get() = R.drawable.my_assistant_ic_notify_player

    override val playerCoverPlaceholderRes: Int
        get() = R.drawable.my_assistant_ic_player_cover_placeholder

    override val controlPreviousLabelRes: Int
        get() = R.string.my_assistant_player_notification_skip_to_previous

    override val controlNextLabelRes: Int
        get() = R.string.my_assistant_player_notification_skip_to_next

    override val controlPlayLabelRes: Int
        get() = R.string.my_assistant_player_notification_play

    override val controlPauseLabelRes: Int
        get() = R.string.my_assistant_player_notification_pause
}