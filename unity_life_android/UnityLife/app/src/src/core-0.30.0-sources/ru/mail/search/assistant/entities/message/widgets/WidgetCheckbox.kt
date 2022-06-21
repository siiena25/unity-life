package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

data class WidgetCheckbox(
    val suggest: Suggest,
    val statisticEvent: WidgetsStatisticEvent?
)