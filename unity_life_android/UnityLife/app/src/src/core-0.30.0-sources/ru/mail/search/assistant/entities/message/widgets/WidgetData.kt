package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

class WidgetData(
    val title: String?,
    val subtitle: String?,
    val suggest: Suggest?,
    val lightIconUrl: String?,
    val darkIconUrl: String?,
    val counter: Int,
    val badge: String?,
    val color: Int?,
    val statisticEvent: WidgetsStatisticEvent?
)