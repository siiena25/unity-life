package ru.mail.search.assistant.entities.message.welcome

import ru.mail.search.assistant.api.suggests.Suggest

data class WelcomeShortcut(
    val title: String?,
    val suggest: Suggest,
    val lightIconUrl: String?,
    val darkIconUrl: String?,
    val promo: WelcomeShortcutPromo?
)