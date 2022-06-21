package ru.mail.search.assistant.entities.message.welcome

data class WelcomeWeather(
    val temperature: Int,
    val subtitle: String?,
    val iconUrl: String?
)