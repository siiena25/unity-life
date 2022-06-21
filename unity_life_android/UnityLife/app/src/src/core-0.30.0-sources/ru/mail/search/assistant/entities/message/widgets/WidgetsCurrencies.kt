package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

data class WidgetsCurrencies(
    val suggest: Suggest?,
    val statisticEvent: WidgetsStatisticEvent?,
    val currencies: List<WidgetsCurrency>
)