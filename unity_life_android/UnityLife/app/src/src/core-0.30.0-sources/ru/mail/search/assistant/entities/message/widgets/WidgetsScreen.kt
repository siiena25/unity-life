package ru.mail.search.assistant.entities.message.widgets

data class WidgetsScreen(
    val title: String,
    val subtitle: String,
    val weather: WidgetsWeather?,
    val currencies: WidgetsCurrencies?,
    val promo: WidgetData?,
    val groups: List<WidgetsGroup>
)