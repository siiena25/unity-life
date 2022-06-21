package ru.mail.search.assistant.entities.message.widgets

import ru.mail.search.assistant.api.suggests.Suggest

data class ListWidgetItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val suggest: Suggest?,
    val checkbox: WidgetCheckbox?,
    val isExpired: Boolean,
    val statisticEvent: WidgetsStatisticEvent?,
    val isChecked: Boolean = false
)