package ru.mail.search.assistant.entities.message.welcome

import ru.mail.search.assistant.entities.message.widgets.WidgetsCurrency

data class WelcomeScreen(
    val title: String?,
    val subtitle: String?,
    val weather: WelcomeWeather?,
    val currencies: List<WidgetsCurrency>,
    val miniCards: List<WelcomeSkillCard>,
    val shortcuts: List<WelcomeShortcut>
)