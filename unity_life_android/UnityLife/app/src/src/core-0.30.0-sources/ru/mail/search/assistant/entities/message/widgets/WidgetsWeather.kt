package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

data class WidgetsWeather(
    val temperature: Int,
    val iconUrl: String,
    val suggest: Suggest?,
    val statisticEvent: WidgetsStatisticEvent?
)