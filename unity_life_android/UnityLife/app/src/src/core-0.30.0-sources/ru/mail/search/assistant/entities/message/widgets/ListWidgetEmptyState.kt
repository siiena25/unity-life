package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

data class ListWidgetEmptyState(
    val text: String?,
    val suggest: Suggest?,
    val button: WidgetButton?
)