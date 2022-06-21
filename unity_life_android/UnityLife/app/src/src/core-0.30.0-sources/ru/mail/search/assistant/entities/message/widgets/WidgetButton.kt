package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

data class WidgetButton(
    val text: String,
    val suggest: Suggest?,
    val statisticEvent: WidgetsStatisticEvent?
)