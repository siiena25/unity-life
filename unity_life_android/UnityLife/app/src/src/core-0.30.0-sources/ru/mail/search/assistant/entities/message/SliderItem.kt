package ru.mail.search.assistant.entities.message

data class SliderItem(
    val title: String?,
    val subtitle: String?,
    val imageUrl: String?,
    val url: String?,
    val eventType: String?,
    val searchQueryText: String?,
    val searchQueryParam: String?,
    val callbackData: String?
)